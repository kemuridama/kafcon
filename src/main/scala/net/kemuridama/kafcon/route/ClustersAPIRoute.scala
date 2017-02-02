package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, APIError, Cluster}
import net.kemuridama.kafcon.service.{UsesClusterService, MixinClusterService}
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}
import net.kemuridama.kafcon.protocol.{APIResponseJsonProtocol, ClusterJsonProtocol}

trait ClustersAPIRoute
  extends UsesClusterService
  with UsesBrokerService
  with UsesTopicService
  with UsesApplicationConfig
  with APIResponseJsonProtocol
  with ClusterJsonProtocol {

  private lazy val clusterName = applicationConfig.cluster.getString("name")

  val route = pathPrefix("clusters") {
    pathEnd{
      get {
        clusterService.getCluster(1) match {
          case Some(cluster) => complete(APIResponse(Some(Cluster(
            cluster.name,
            cluster.zookeepers,
            brokerService.getAll,
            topicService.getAll,
            topicService.getAll.map(_.partitions.size).foldLeft(0L)((sum, partitionCount) => sum + partitionCount),
            topicService.getAll.map(_.messageCount).foldLeft(0L)((sum, messageCount) => sum + messageCount),
            cluster.getConnectionState
          ))))
          case _ => complete(StatusCodes.NotFound, APIResponse[Unit](error = Some(APIError(message = Some("Not found")))))
        }
      }
    }
  }

}

private[route] object ClustersAPIRoute
  extends ClustersAPIRoute
  with MixinClusterService
  with MixinBrokerService
  with MixinTopicService
  with MixinApplicationConfig

trait UsesClustersAPIRoute {
  val clustersAPIRoute: ClustersAPIRoute
}

trait MixinClustersAPIRoute {
  val clustersAPIRoute = ClustersAPIRoute
}
