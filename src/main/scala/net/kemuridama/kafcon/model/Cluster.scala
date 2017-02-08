package net.kemuridama.kafcon.model

import spray.json._
import kafka.utils.ZkUtils

import net.kemuridama.kafcon.protocol.ZooKeeperBrokerJsonProtocol

case class Cluster(
  id: Int,
  name: String,
  zookeepers: List[String]
) extends ZooKeeperBrokerJsonProtocol {

  private val sessionTimeout = 10 * 1000
  private val connectionTimeout = 10 * 1000

  private var connectionState: ConnectionState = ConnectionState.Disconnected

  private def brokerPath(id: Int) = "/brokers/ids/%d".format(id)

  def withZkUtils[T](func: ZkUtils => T): Option[T] = {
    try {
      val zkUtils = ZkUtils(zookeepers.mkString(","), sessionTimeout, connectionTimeout, false)
      val ret = func(zkUtils)
      zkUtils.close
      Some(ret)
    } catch {
      case _: Throwable => None
    }
  }

  def getConnectionState: ConnectionState = connectionState

  def getAllBrokers: List[Broker] = withZkUtils { zk =>
    zk.getAllBrokersInCluster.toList.map { broker =>
      val (data, stat) = zk.readDataMaybeNull(brokerPath(broker.id))
      data.map(_.parseJson.convertTo[ZooKeeperBroker].toBroker(id, broker.id))
    } flatten
  } getOrElse(List.empty[Broker])

  def getAllTopics: List[String] = withZkUtils { zk =>
    zk.getAllTopics.toList
  } getOrElse(List.empty[String])

  def toClusterResponseData(brokers: List[Broker], topics: List[Topic]): ClusterResponseData = {
    ClusterResponseData(
      id              = id,
      name            = name,
      zookeepers      = zookeepers,
      brokers         = brokers,
      topics          = topics,
      partitionCount  = topics.map(_.partitions.size).sum,
      messageCount    = topics.map(_.messageCount).sum,
      connectionState = getConnectionState
    )
  }

}
