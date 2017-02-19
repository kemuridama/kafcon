package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.service.{UsesClusterService, MixinClusterService}
import net.kemuridama.kafcon.protocol.ClusterResponseDataJsonProtocol

trait ClustersAPIRoute
  extends APIRoute
  with UsesClusterService
  with ClusterResponseDataJsonProtocol {

  val route = pathPrefix("clusters" / IntNumber) { id =>
    pathEnd{
      get {
        onSuccess(clusterService.getAllClusterResponseData) { response =>
          complete(APIResponse(Some(response)))
        }
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEnd {
        get {
          onSuccess(clusterService.getClusterResponseData(id)) {
            case Some(response) => complete(APIResponse(Some(response)))
            case _ => complete(StatusCodes.NotFound, errorMessage("Not found"))
          }
        }
      }
    }
  }

}

private[route] object ClustersAPIRoute
  extends ClustersAPIRoute
  with MixinClusterService

trait UsesClustersAPIRoute {
  val clustersAPIRoute: ClustersAPIRoute
}

trait MixinClustersAPIRoute {
  val clustersAPIRoute = ClustersAPIRoute
}
