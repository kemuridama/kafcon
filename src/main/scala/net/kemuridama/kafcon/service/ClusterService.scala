package net.kemuridama.kafcon.service

import net.kemuridama.kafcon.model.Cluster
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

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

private[service] object ClusterService
  extends ClusterService
  with MixinApplicationConfig

trait UsesClusterService {
  val clusterService: ClusterService
}

trait MixinClusterService {
  val clusterService = ClusterService
}
