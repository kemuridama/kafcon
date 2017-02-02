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

  private var zkUtils: Option[ZkUtils] = None
  private var connectionState: ConnectionState = ConnectionState.Disconnected

  private def brokerPath(id: Int) = "/brokers/ids/%d".format(id)

  private def connect: Unit = {
    zkUtils = Some(ZkUtils(zookeepers.mkString(","), sessionTimeout, connectionTimeout, false))
  }

  private def disconnect: Unit = {
    zkUtils.map(_.close)
    zkUtils = None
  }

  private def getConnection = zkUtils match {
    case Some(zk) => Some(zk)
    case _ => {
      connect
      zkUtils
    }
  }

  private def withZkUtils[T](func: ZkUtils => T): Option[T] = getConnection.flatMap { zk =>
    Some(func(zk))
  }

  def getConnectionState: ConnectionState = connectionState

  def getAllBrokers: List[Broker] = withZkUtils { zk =>
    zk.getAllBrokersInCluster.toList.map { broker =>
      val (data, stat) = zk.readDataMaybeNull(brokerPath(broker.id))
      data.map(_.parseJson.convertTo[ZooKeeperBroker].toBroker(broker.id))
    } flatten
  } getOrElse(List.empty[Broker])

  def getAllTopics: List[String] = withZkUtils { zk =>
    zk.getAllTopics.toList
  } getOrElse(List.empty[String])

}
