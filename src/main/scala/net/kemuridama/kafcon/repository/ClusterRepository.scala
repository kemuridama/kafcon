package net.kemuridama.kafcon.repository

import scala.concurrent.Future

import net.kemuridama.kafcon.model.Cluster

trait ClusterRepository {

  private var clusters = List.empty[Cluster]

  def insert(cluster: Cluster): Future[Unit] = Future.successful {
    clusters :+= cluster
  }

  def all: Future[List[Cluster]] = Future.successful(clusters)

  def find(id: Int): Future[Option[Cluster]] = Future.successful(clusters.find(_.id == id))

}

private[repository] object ClusterRepository
  extends ClusterRepository

trait UsesClusterRepository {
  val clusterRepository: ClusterRepository
}

trait MixinClusterRepository {
  val clusterRepository = ClusterRepository
}
