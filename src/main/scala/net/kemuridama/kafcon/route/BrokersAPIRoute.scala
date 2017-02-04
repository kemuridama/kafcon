package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.protocol.BrokerJsonProtocol

trait BrokersAPIRoute
  extends APIRoute
  with UsesBrokerService
  with BrokerJsonProtocol {

  val route = pathPrefix("brokers") {
    pathEnd {
      get {
        complete(APIResponse(Some(brokerService.findAll(1))))
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEnd {
        get {
          brokerService.find(1, id) match {
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
