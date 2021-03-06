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
        id         = index + 1,
        name       = config.getString("name"),
        zookeepers = config.getStringList("zookeepers").toList
      ))
    }
  }

  def update = {
    all.foreach { clusters =>
      clusters.foreach { cluster =>
        brokerService.update(cluster)
        topicService.update(cluster)
      }
    }
  }

  def all: Future[List[Cluster]] = clusterRepository.all
  def find(id: Int = 1): Future[Option[Cluster]] = clusterRepository.find(id)

  def getAllClusterResponseData: Future[List[ClusterResponseData]] = {
    all.flatMap { clusters =>
      Future.sequence(clusters.map { cluster =>
        for {
          brokers <- brokerService.findAll(cluster.id)
          topics  <- topicService.findAll(cluster.id)
        } yield cluster.toClusterResponseData(brokers, topics)
      })
    }
  }

  def getClusterResponseData(id: Int): Future[Option[ClusterResponseData]] = {
    (for {
      Some(cluster) <- find(id)
      brokers       <- brokerService.findAll(cluster.id)
      topics        <- topicService.findAll(cluster.id)
    } yield Some(cluster.toClusterResponseData(brokers, topics))).recoverWith {
      case _: Throwable => Future.successful(None)
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
