package net.kemuridama.kafcon.service

import kafka.cluster.BrokerEndPoint

import net.kemuridama.kafcon.model.Cluster
import net.kemuridama.kafcon.repository.{UsesClusterRepository, MixinClusterRepository}
import net.kemuridama.kafcon.repository.{UsesBrokerRepository, MixinBrokerRepository}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

trait ClusterService
  extends UsesClusterRepository
  with UsesBrokerRepository
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

  def getBrokerEndPoints(id: Int): List[BrokerEndPoint] = {
    brokerRepository.findAll(id).map(_.toBrokerEndPoint)
  }

}

private[service] object ClusterService
  extends ClusterService
  with MixinClusterRepository
  with MixinBrokerRepository
  with MixinApplicationConfig

trait UsesClusterService {
  val clusterService: ClusterService
}

trait MixinClusterService {
  val clusterService = ClusterService
}
