package net.kemuridama.kafcon.service

import kafka.api.PartitionMetadata
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Topic, Partition, PartitionOffset}

trait TopicService
  extends UsesZooKeeperService
  with UsesBrokerService
  with UsesConsumerService {

  private val topicsPath = "/brokers/topics"
  private def topicPath(name: String) = "/brokers/topics/%s".format(name)

  private var topics = List.empty[Topic]

  def update: Unit = {
    val topicNames = zookeeperService.getChildren(topicsPath)
    topics = brokerService.fetchTopicMetadata(topicNames).map { topicMetadata =>
      val partitions = topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
        Partition(
          partitionMetadata.partitionId,
          partitionMetadata.leader.map(_.id),
          partitionMetadata.replicas.toList.map(_.id),
          partitionMetadata.isr.toList.map(_.id),
          getPartitionOffset(topicMetadata.topic, partitionMetadata)
        )
      } sortBy(_.id)

      Topic(
        topicMetadata.topic,
        partitions.flatMap(_.replicas).distinct,
        partitions.map(_.replicas.size).max,
        partitions.foldLeft(0L)((sum, partition) => sum + partition.getMessageCount),
        partitions
      )
    }
  }

  def getAll: List[Topic] = topics
  def get(name: String): Option[Topic] = topics.find(_.name == name)

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
  with MixinZooKeeperService
  with MixinBrokerService
  with MixinConsumerService

trait UsesTopicService {
  val topicService: TopicService
}

trait MixinTopicService {
  val topicService = TopicService
}
