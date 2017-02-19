package net.kemuridama.kafcon.service

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

  def all: Future[List[Cluster]] = clusterRepository.all
  def find(id: Int = 1): Future[Option[Cluster]] = clusterRepository.find(id)

  def getAllClusterResponseData: Future[List[ClusterResponseData]] = {
    all.map { clusters =>
      clusters.map { cluster =>
        val brokers = brokerService.findAll(cluster.id)
        val topics = topicService.findAll(cluster.id)
        cluster.toClusterResponseData(brokers, topics)
      }
    }
  }

  def getClusterResponseData(id: Int): Future[Option[ClusterResponseData]] = {
    find(id).map { clusterOpt =>
      clusterOpt.map { cluster =>
        val brokers = brokerService.findAll(cluster.id)
        val topics = topicService.findAll(cluster.id)
        cluster.toClusterResponseData(brokers, topics)
      }
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
