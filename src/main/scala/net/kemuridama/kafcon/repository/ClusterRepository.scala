package net.kemuridama.kafcon.repository

import net.kemuridama.kafcon.model.Cluster

trait ClusterRepository {

  private var clusters = List.empty[Cluster]

  def insert(cluster: Cluster): Unit = {
    clusters :+= cluster
  }

  def all: List[Cluster] = clusters

  def find(id: Int): Option[Cluster] = clusters.find(_.id == id)

}

private[repository] object ClusterRepository
  extends ClusterRepository

trait UsesClusterRepository {
  val clusterRepository: ClusterRepository
}

trait MixinClusterRepository {
  val clusterRepository = ClusterRepository
}
