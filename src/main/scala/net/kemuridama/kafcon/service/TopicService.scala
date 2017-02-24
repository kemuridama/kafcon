package net.kemuridama.kafcon.service

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import kafka.client.ClientUtils
import kafka.admin.AdminUtils
import kafka.api.TopicMetadata
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Cluster, Broker, Topic, Partition, PartitionOffset}
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
    } yield {
      topicsMetadata.map { topicMetadata =>
        val partitions = fetchPartitions(clusterId, topicMetadata)
        Topic(
          name              = topicMetadata.topic,
          clusterId         = clusterId,
          brokers           = partitions.flatMap(_.replicas).distinct,
          replicationFactor = partitions.map(_.replicas.size).max,
          messageCount      = partitions.foldLeft(0L)((sum, partition) => sum + partition.getMessageCount),
          partitions        = partitions
        )
      }
    }
  }

  private def fetchTopicMetadata(brokers: List[Broker], topicNames: List[String]): Future[List[TopicMetadata]] = Future {
    ClientUtils.fetchTopicMetadata(topicNames.toSet, brokers.map(_.toBrokerEndPoint), "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList
  }

  private def fetchPartitions(clusterId: Int, topicMetadata: TopicMetadata): List[Partition] = {
    topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
      val offset: Option[PartitionOffset] = partitionMetadata.leader.flatMap { leader =>
        Await.result(brokerService.find(clusterId, leader.id).flatMap {
          case Some(broker) => fetchPartition(broker, topicMetadata.topic, partitionMetadata.partitionId)
          case _            => Future.failed(sys.error("Leader not found"))
        }, Duration.Inf)
      }

      Partition(
        id          = partitionMetadata.partitionId,
        leader      = partitionMetadata.leader.map(_.id),
        replicas    = partitionMetadata.replicas.toList.map(_.id),
        isr         = partitionMetadata.isr.toList.map(_.id),
        firstOffset = offset.map(_.first),
        lastOffset  = offset.map(_.last)
      )
    }
  }

  private def fetchPartition(broker: Broker, topicName: String, partitionId: Int): Future[Option[PartitionOffset]] = {
    broker.withSimpleConsumer { consumer =>
      val tap = TopicAndPartition(topicName, partitionId)
      Some(PartitionOffset(
        first = consumer.earliestOrLatestOffset(tap, -2, 1),
        last  = consumer.earliestOrLatestOffset(tap, -1, 2)
      ))
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
