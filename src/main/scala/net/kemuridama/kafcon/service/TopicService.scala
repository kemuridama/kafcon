package net.kemuridama.kafcon.service

import kafka.client.ClientUtils
import kafka.api.PartitionMetadata
import kafka.common.TopicAndPartition

import net.kemuridama.kafcon.model.{Topic, Partition, PartitionOffset}
import net.kemuridama.kafcon.repository.{UsesTopicRepository, MixinTopicRepository}

trait TopicService
  extends UsesTopicRepository
  with UsesClusterService
  with UsesBrokerService
  with UsesConsumerService {

  def update: Unit = {
    clusterService.all.foreach { cluster =>
      val topicNames = cluster.getAllTopics.toSet
      ClientUtils.fetchTopicMetadata(topicNames, brokerService.findAll(cluster.id).map(_.toBrokerEndPoint), "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.foreach { topicMetadata =>
        val partitions = topicMetadata.partitionsMetadata.toList.map { partitionMetadata =>
          Partition(
            id       = partitionMetadata.partitionId,
            leader   = partitionMetadata.leader.map(_.id),
            replicas = partitionMetadata.replicas.toList.map(_.id),
            isr      = partitionMetadata.isr.toList.map(_.id),
            offset   = getPartitionOffset(topicMetadata.topic, partitionMetadata)
          )
        } sortBy(_.id)

        topicRepository.insert(Topic(
          name              = topicMetadata.topic,
          clusterId         = cluster.id,
          brokers           = partitions.flatMap(_.replicas).distinct,
          replicationFactor = partitions.map(_.replicas.size).max,
          messageCount      = partitions.foldLeft(0L)((sum, partition) => sum + partition.getMessageCount),
          partitions        = partitions
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
