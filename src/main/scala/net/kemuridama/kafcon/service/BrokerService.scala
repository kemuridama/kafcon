package net.kemuridama.kafcon.service

import javax.management._
import javax.management.remote._

import spray.json._
import kafka.client.ClientUtils
import kafka.cluster.BrokerEndPoint
import kafka.api.TopicMetadata

import net.kemuridama.kafcon.model.{Broker, ZooKeeperBroker}
import net.kemuridama.kafcon.protocol.ZooKeeperBrokerJsonProtocol
import net.kemuridama.kafcon.repository.{UsesBrokerRepository, MixinBrokerRepository}

trait BrokerService
  extends UsesBrokerRepository
  with UsesClusterService
  with ZooKeeperBrokerJsonProtocol {

  private val brokersPath = "/brokers/ids"
  private def brokerPath(id: Int) = "/brokers/ids/%d".format(id)

  def update: Unit = {
    clusterService.find(1).map { cluster =>
      brokerRepository.insert(cluster.getAllBrokers)
    }
  }

  def find(clusterId: Int, id: Int): Option[Broker] = brokerRepository.find(clusterId, id)
  def findAll(clusterId: Int): List[Broker] = brokerRepository.findAll(clusterId)

  def fetchTopicMetadata(clusterId: Int, topicList: List[String]): List[TopicMetadata] = {
    if (findAll(clusterId).nonEmpty) ClientUtils.fetchTopicMetadata(topicList.toSet, clusterService.getBrokerEndPoints(clusterId), "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList
    else List.empty[TopicMetadata]
  }

}

object BrokerService
  extends BrokerService
  with MixinBrokerRepository
  with MixinClusterService

trait UsesBrokerService {
  val brokerService: BrokerService
}

trait MixinBrokerService {
  val brokerService = BrokerService
}
