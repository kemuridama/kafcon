package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.protocol.{APIResponseJsonProtocol, BrokerJsonProtocol}

trait BrokersAPIRoute
  extends UsesBrokerService
  with APIResponseJsonProtocol
  with BrokerJsonProtocol {

  val route = pathPrefix("brokers") {
    pathEnd {
      get {
        complete(APIResponse(brokerService.getAll))
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEnd {
        get {
          brokerService.get(id) match {
            case Some(broker) => complete(APIResponse(broker))
            case _ => complete(StatusCodes.NotFound)
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
