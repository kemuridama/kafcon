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
  private lazy val zookeeper = new ZooKeeper(zookeeperServers.mkString(","), sessionTimeout, watcher)

  private var connectionState: ConnectionState = ConnectionState.Disconnected

  def getAll: List[String] = zookeeperServers
  def getConnectionState: ConnectionState = connectionState

  def getChildren(path: String): List[String] = try {
    zookeeper.getChildren(path, false).toList
  } catch {
    case _: Exception => {
      connectionState = ConnectionState.Disconnected
      List.empty[String]
    }
  }

  def getData(path: String): String = try {
    new String(zookeeper.getData(path, false, new Stat), charset)
  } catch {
    case _: Exception => {
      connectionState = ConnectionState.Disconnected
      ""
    }
  }

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
