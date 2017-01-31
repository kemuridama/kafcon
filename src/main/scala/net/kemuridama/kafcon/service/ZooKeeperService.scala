package net.kemuridama.kafcon.service

import org.apache.zookeeper.{ZooKeeper, Watcher, WatchedEvent}
import org.apache.zookeeper.data.Stat

import net.kemuridama.kafcon.model.ConnectionState
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

trait ZooKeeperService
  extends UsesApplicationConfig {

  import collection.JavaConversions._

  private val sessionTimeout = 10 * 1000
  private val charset = "UTF-8"

  private val watcher = new Watcher {
    def process(event: WatchedEvent) = { /* Ignore all watched event */ }
  }

  private lazy val zookeeperServers = applicationConfig.cluster.getStringList("zookeeperServers").toList
  private var zookeeper: Option[ZooKeeper] = None

  private var connectionState: ConnectionState = ConnectionState.Disconnected

  private def connect: Option[ZooKeeper] = {
    connectionState = ConnectionState.Connected
    Some(new ZooKeeper(zookeeperServers.mkString(","), sessionTimeout, watcher))
  }

  private def disconnect: Unit = {
    connectionState = ConnectionState.Disconnected
    zookeeper.map(_.close)
  }

  private def getConnection: Option[ZooKeeper] = zookeeper match {
    case Some(zk) => Some(zk)
    case _ => {
      zookeeper = connect
      zookeeper
    }
  }

  private def withConnection[T](func: ZooKeeper => T): Option[T] = getConnection.flatMap { zk =>
    try {
      Some(func(zk))
    } catch {
      case _: Exception => {
        disconnect
        None
      }
    }
  }

  def getAll: List[String] = zookeeperServers
  def getConnectionState: ConnectionState = connectionState

  def getChildren(path: String): List[String] = withConnection { zk =>
    zk.getChildren(path, false).toList
  } getOrElse(List.empty[String])

  def getData(path: String): String = withConnection { zk =>
    new String(zk.getData(path, false, new Stat), charset)
  } getOrElse("")

}

private[service] object ZooKeeperService
  extends ZooKeeperService
  with MixinApplicationConfig

trait UsesZooKeeperService {
  val zookeeperService: ZooKeeperService
}

trait MixinZooKeeperService {
  val zookeeperService = ZooKeeperService
}
