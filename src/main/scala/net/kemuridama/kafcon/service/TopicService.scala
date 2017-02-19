package net.kemuridama.kafcon.service

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import kafka.client.ClientUtils
import kafka.api.TopicMetadata
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Topic, Partition, PartitionOffset}
import net.kemuridama.kafcon.repository.{UsesTopicRepository, MixinTopicRepository}

trait TopicService
  extends UsesTopicRepository
  with UsesClusterService
  with UsesBrokerService {

  def update: Unit = {
    clusterService.all.foreach { clusters =>
      clusters.foreach { cluster =>
        cluster.getAllTopics.foreach { topicNames =>
          fetchTopics(cluster.id, topicNames).foreach { topic =>
            topicRepository.insert(topic)
          }
        }
      }
    }
  }

  def findAll(clusterId: Int): List[Topic] = topicRepository.findAll(clusterId)
  def find(clusterId: Int, name: String): Option[Topic] = topicRepository.find(clusterId, name)

  private def fetchTopics(clusterId: Int, topicNames: List[String]): List[Topic] = {
    val brokers = brokerService.findAll(clusterId)
    ClientUtils.fetchTopicMetadata(topicNames.toSet, brokers.map(_.toBrokerEndPoint), "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList.map { topicMetadata =>
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

  private def fetchPartitions(clusterId: Int, topicMetadata: TopicMetadata): List[Partition] = {
    topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
      val offset = for {
        leader <- partitionMetadata.leader
        broker <- brokerService.find(clusterId, leader.id)
      } yield {
        val offset = broker.withSimpleConsumer { consumer =>
          val tap = TopicAndPartition(topicMetadata.topic, partitionMetadata.partitionId)
          PartitionOffset(
            first = consumer.earliestOrLatestOffset(tap, -2, 1),
            last  = consumer.earliestOrLatestOffset(tap, -1, 2)
          )
        }
        Await.result(offset, Duration.Inf)
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
