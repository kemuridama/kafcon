package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, ClusterResponseData}
import net.kemuridama.kafcon.service.{UsesClusterService, MixinClusterService}
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}
import net.kemuridama.kafcon.protocol.ClusterResponseDataJsonProtocol

trait ClustersAPIRoute
  extends APIRoute
  with UsesClusterService
  with UsesBrokerService
  with UsesTopicService
  with ClusterResponseDataJsonProtocol {

  val route = pathPrefix("clusters") {
    pathEnd{
      get {
        clusterService.find(1) match {
          case Some(cluster) => complete(APIResponse(Some(ClusterResponseData(
            cluster.id,
            cluster.name,
            cluster.zookeepers,
            brokerService.findAll(cluster.id),
            topicService.findAll(1),
            topicService.findAll(1).map(_.partitions.size).foldLeft(0L)((sum, partitionCount) => sum + partitionCount),
            topicService.findAll(1).map(_.messageCount).foldLeft(0L)((sum, messageCount) => sum + messageCount),
            cluster.getConnectionState
          ))))
          case _ => complete(StatusCodes.NotFound, errorMessage("Not found"))
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

trait UsesClustersAPIRoute {
  val clustersAPIRoute: ClustersAPIRoute
}

trait MixinClustersAPIRoute {
  val clustersAPIRoute = ClustersAPIRoute
}
