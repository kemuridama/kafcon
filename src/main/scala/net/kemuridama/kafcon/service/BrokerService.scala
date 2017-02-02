package net.kemuridama.kafcon.service

import javax.management._
import javax.management.remote._

import spray.json._
import kafka.client.ClientUtils
import kafka.cluster.BrokerEndPoint
import kafka.api.TopicMetadata

import net.kemuridama.kafcon.model.{Broker, ZooKeeperBroker}
import net.kemuridama.kafcon.protocol.ZooKeeperBrokerJsonProtocol

trait BrokerService
  extends UsesClusterService
  with ZooKeeperBrokerJsonProtocol {

  private val brokersPath = "/brokers/ids"
  private def brokerPath(id: Int) = "/brokers/ids/%d".format(id)

  private var brokers = List.empty[Broker]

  def update: Unit = {
    brokers = clusterService.getCluster(1).map { cluster =>
      cluster.getAllBrokers
    } getOrElse(List.empty[Broker])
  }

  def get(id: Int): Option[Broker] = brokers.filter(_.id == id).headOption
  def getAll: List[Broker] = brokers

  def fetchTopicMetadata(topicList: List[String]): List[TopicMetadata] = {
    if (brokers.nonEmpty) ClientUtils.fetchTopicMetadata(topicList.toSet, getBrokerEndPoints, "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList
    else List.empty[TopicMetadata]
  }

  private def getBrokerEndPoints: List[BrokerEndPoint] = brokers.map(_.toBrokerEndPoint)

}

object BrokerService
  extends BrokerService
  with MixinClusterService

trait UsesBrokerService {
  val brokerService: BrokerService
}

trait MixinBrokerService {
  val brokerService = BrokerService
}
