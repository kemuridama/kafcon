package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.protocol.BrokerJsonProtocol

trait BrokersAPIRoute
  extends APIRoute
  with UsesBrokerService
  with BrokerJsonProtocol {

  val route = pathPrefix("clusters" / IntNumber / "brokers") { clusterId =>
    pathEnd {
      get {
        complete(APIResponse(Some(brokerService.findAll(clusterId))))
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEnd {
        get {
          brokerService.find(clusterId, id) match {
            case Some(broker) => complete(APIResponse(Some(broker)))
            case _ => complete(StatusCodes.NotFound, errorMessage("Not found"))
          }
        }
      }
    }
  }

}

private[route] object BrokersAPIRoute
  extends BrokersAPIRoute
  with MixinBrokerService

trait UsesBrokersAPIRoute {
  val brokersAPIRoute: BrokersAPIRoute
}

trait MixinBrokersAPIRoute {
  val brokersAPIRoute = BrokersAPIRoute
}
