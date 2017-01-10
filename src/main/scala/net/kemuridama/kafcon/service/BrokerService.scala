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
  extends UsesZooKeeperService
  with ZooKeeperBrokerJsonProtocol {

  private val brokersPath = "/brokers/ids"
  private def brokerPath(id: Int) = "/brokers/ids/%d".format(id)

  private var brokers = List.empty[Broker]

  def update: Unit = {
    brokers = zookeeperService.getChildren(brokersPath).map(_.toInt).map { id =>
      val zkBroker = zookeeperService.getData(brokerPath(id)).parseJson.convertTo[ZooKeeperBroker]
      zkBroker.toBroker(id)
    }
  }

  def get(id: Int): Option[Broker] = brokers.filter(_.id == id).headOption
  def getAll: List[Broker] = brokers

  def fetchTopicMetadata(topicList: List[String]): List[TopicMetadata] = {
    ClientUtils.fetchTopicMetadata(topicList.toSet, getBrokerEndPoints, "kafcon-topic-metadata-fetcher", 1000).topicsMetadata.toList
  }

  private def getBrokerEndPoints: List[BrokerEndPoint] = brokers.map(_.toBrokerEndPoint)

}

object BrokerService
  extends BrokerService
  with MixinZooKeeperService

trait UsesBrokerService {
  val brokerService: BrokerService
}

trait MixinBrokerService {
  val brokerService = BrokerService
}
