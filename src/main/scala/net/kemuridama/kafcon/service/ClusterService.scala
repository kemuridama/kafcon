package net.kemuridama.kafcon.service

import spray.json._
import kafka.utils.ZkUtils
import org.joda.time.DateTime

import net.kemuridama.kafcon.model.{ConnectionState, Broker, ZooKeeperBroker}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}
import net.kemuridama.kafcon.protocol.ZooKeeperBrokerJsonProtocol

trait ClusterService
  extends UsesApplicationConfig {

  import collection.JavaConversions._

  private lazy val clusters: List[Cluster] = {
    applicationConfig.clusters.toList.zipWithIndex.map { case (config, index) =>
      Cluster(
        index + 1,
        config.getString("name"),
        config.getStringList("zookeepers").toList
      )
    }
  }

  def getCluster(id: Int = 1): Option[Cluster] = clusters.filter(_.id == id).headOption

}

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

private[service] object ClusterService
  extends ClusterService
  with MixinApplicationConfig

trait UsesClusterService {
  val clusterService: ClusterService
}

trait MixinClusterService {
  val clusterService = ClusterService
}
