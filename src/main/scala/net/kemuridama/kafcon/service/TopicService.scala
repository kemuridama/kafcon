package net.kemuridama.kafcon.service

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import kafka.client.ClientUtils
import kafka.admin.AdminUtils
import kafka.api.{TopicMetadata, PartitionMetadata}
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Cluster, Broker, Topic, Partition}
import net.kemuridama.kafcon.repository.{UsesTopicRepository, MixinTopicRepository}

trait TopicService
  extends UsesTopicRepository
  with UsesClusterService
  with UsesBrokerService {

  def update(cluster: Cluster): Unit = {
    cluster.getAllTopics.foreach { topicNames =>
      fetchTopics(cluster.id, topicNames).foreach { topics =>
        topics.foreach { topic =>
          topicRepository.insert(topic)
        }
      }
    }
  }

  def findAll(clusterId: Int): Future[List[Topic]] = topicRepository.findAll(clusterId)
  def find(clusterId: Int, name: String): Future[Option[Topic]] = topicRepository.find(clusterId, name)

  def create(clusterId: Int, name: String, replicationFactor: Int, partitionCount: Int): Future[Topic] = {
    for {
      Some(cluster) <- clusterService.find(clusterId)
      _ <- cluster.withZkUtils(zkUtils => AdminUtils.createTopic(zkUtils, name, partitionCount, replicationFactor))
      Some(topic) <- fetchTopics(clusterId, List(name)).map(_.headOption)
    } yield topic
  }

  private def fetchTopics(clusterId: Int, topicNames: List[String]): Future[List[Topic]] = {
    for {
      brokers        <- brokerService.findAll(clusterId)
      topicsMetadata <- fetchTopicMetadata(brokers, topicNames)
      topics         <- Future.sequence(topicsMetadata.map { topicMetadata =>
        fetchPartitions(clusterId, topicMetadata).map { partitions =>
          Topic(
            name              = topicMetadata.topic,
            clusterId         = clusterId,
            brokers           = partitions.flatMap(_.replicas).distinct,
            replicationFactor = partitions.map(_.replicas.size).max,
            messageCount      = partitions.foldLeft(0L)((sum, partition) => sum + partition.getMessageCount),
            partitions        = partitions
          )
        }
      })
    } yield topics
  }

  private def fetchTopicMetadata(brokers: List[Broker], topicNames: List[String]): Future[List[TopicMetadata]] = Future {
    ClientUtils.fetchTopicMetadata(topicNames.toSet, brokers.map(_.toBrokerEndPoint), "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList
  }

  private def fetchPartitions(clusterId: Int, topicMetadata: TopicMetadata): Future[List[Partition]] = {
    Future.sequence(topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
      fetchOffset(clusterId, topicMetadata, partitionMetadata).map { offset =>
        Partition(
          id          = partitionMetadata.partitionId,
          leader      = partitionMetadata.leader.map(_.id),
          replicas    = partitionMetadata.replicas.toList.map(_.id),
          isr         = partitionMetadata.isr.toList.map(_.id),
          firstOffset = offset.map(_._1),
          lastOffset  = offset.map(_._2)
        )
      }
    })
  }

  private def fetchOffset(clusterId: Int, topicMetadata: TopicMetadata, partitionMetadata: PartitionMetadata): Future[Option[(Long, Long)]] = {
    (for {
      Some(leader) <- Future.successful(partitionMetadata.leader)
      Some(broker) <- brokerService.find(clusterId, leader.id)
      offset       <- broker.withSimpleConsumer { consumer =>
        val tap = TopicAndPartition(topicMetadata.topic, partitionMetadata.partitionId)
        Pair(
          consumer.earliestOrLatestOffset(tap, -2, 1),
          consumer.earliestOrLatestOffset(tap, -1, 2)
        )
      }
    } yield Some(offset)).recoverWith {
      case _: Throwable => Future.successful(None)
    }
  }

}

private[service] object TopicService
  extends TopicService
  with MixinTopicRepository
  with MixinClusterService
  with MixinBrokerService

trait UsesTopicService {
  val topicService: TopicService
}

trait MixinTopicService {
  val topicService = TopicService
}
