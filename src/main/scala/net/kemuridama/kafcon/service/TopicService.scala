package net.kemuridama.kafcon.service

import kafka.api.PartitionMetadata
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Topic, Partition, PartitionOffset}
import net.kemuridama.kafcon.repository.{UsesTopicRepository, MixinTopicRepository}

trait TopicService
  extends UsesTopicRepository
  with UsesClusterService
  with UsesBrokerService
  with UsesConsumerService {

  private val topicsPath = "/brokers/topics"
  private def topicPath(name: String) = "/brokers/topics/%s".format(name)

  def update: Unit = {
    clusterService.find(1).map { cluster =>
      val topicNames = cluster.getAllTopics
      brokerService.fetchTopicMetadata(1, topicNames).map { topicMetadata =>
        val partitions = topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
          Partition(
            partitionMetadata.partitionId,
            partitionMetadata.leader.map(_.id),
            partitionMetadata.replicas.toList.map(_.id),
            partitionMetadata.isr.toList.map(_.id),
            getPartitionOffset(topicMetadata.topic, partitionMetadata)
          )
        } sortBy(_.id)

        topicRepository.insert(Topic(
          topicMetadata.topic,
          1,
          partitions.flatMap(_.replicas).distinct,
          partitions.map(_.replicas.size).max,
          partitions.foldLeft(0L)((sum, partition) => sum + partition.getMessageCount),
          partitions
        ))
      }
    }
  }

  def findAll(clusterId: Int): List[Topic] = topicRepository.findAll(clusterId)
  def find(clusterId: Int, name: String): Option[Topic] = topicRepository.find(clusterId, name)

  private def getPartitionOffset(name: String, partitionMetadata: PartitionMetadata): Option[PartitionOffset] = {
    val topicAndPartition = new TopicAndPartition(name, partitionMetadata.partitionId)
    for {
      leader <- partitionMetadata.leader
      consumer <- consumerService.get(leader.id)
    } yield {
      PartitionOffset(
        consumer.earliestOrLatestOffset(topicAndPartition, -2, 1),
        consumer.earliestOrLatestOffset(topicAndPartition, -1, 2)
      )
    }
  }

}

private[service] object TopicService
  extends TopicService
  with MixinTopicRepository
  with MixinClusterService
  with MixinBrokerService
  with MixinConsumerService

trait UsesTopicService {
  val topicService: TopicService
}

trait MixinTopicService {
  val topicService = TopicService
}
