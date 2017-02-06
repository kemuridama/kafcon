package net.kemuridama.kafcon.service

import net.kemuridama.kafcon.model.{Cluster, ClusterResponseData}
import net.kemuridama.kafcon.repository.{UsesClusterRepository, MixinClusterRepository}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

trait ClusterService
  extends UsesClusterRepository
  with UsesBrokerService
  with UsesTopicService
  with UsesApplicationConfig {

  import collection.JavaConversions._

  def init = {
    applicationConfig.clusters.toList.zipWithIndex.map { case (config, index) =>
      clusterRepository.insert(Cluster(
        index + 1,
        config.getString("name"),
        config.getStringList("zookeepers").toList
      ))
    }
  }

  def all: List[Cluster] = clusterRepository.all
  def find(id: Int = 1): Option[Cluster] = clusterRepository.find(id)

  def getAllClusterResponseData: List[ClusterResponseData] = {
    all.map { cluster =>
      val brokers = brokerService.findAll(cluster.id)
      val topics = topicService.findAll(cluster.id)
      cluster.toClusterResponseData(brokers, topics)
    }
  }

  def getClusterResponseData(id: Int): Option[ClusterResponseData] = {
    find(id).map { cluster =>
      val brokers = brokerService.findAll(cluster.id)
      val topics = topicService.findAll(cluster.id)
      cluster.toClusterResponseData(brokers, topics)
    }
  }

}

private[service] object ClusterService
  extends ClusterService
  with MixinClusterRepository
  with MixinBrokerService
  with MixinTopicService
  with MixinApplicationConfig

trait UsesClusterService {
  val clusterService: ClusterService
}

trait MixinClusterService {
  val clusterService = ClusterService
}
