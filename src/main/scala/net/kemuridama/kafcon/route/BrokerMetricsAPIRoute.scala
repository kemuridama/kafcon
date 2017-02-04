package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.service.{UsesBrokerMetricsService, MixinBrokerMetricsService}
import net.kemuridama.kafcon.protocol.{BrokerMetricsJsonProtocol, CombinedBrokerMetricsJsonProtocol}

trait BrokerMetricsAPIRoute
  extends APIRoute
  with UsesBrokerMetricsService
  with BrokerMetricsJsonProtocol
  with CombinedBrokerMetricsJsonProtocol {

  val route = pathPrefix("brokers") {
    pathPrefix("metrics") {
      pathEnd {
        get {
          complete(APIResponse(Some(brokerMetricsService.findByClusterId(1))))
        }
      }
    } ~
    path(IntNumber / "metrics") { id =>
      get {
        brokerMetricsService.findByBrokerId(1, id) match {
          case Some(metrics) => complete(APIResponse(Some(metrics)))
          case _ => complete(StatusCodes.NotFound, errorMessage("Not found"))
        }
      }
    }
  }

}

private[route] object BrokerMetricsAPIRoute
  extends BrokerMetricsAPIRoute
  with MixinBrokerMetricsService

trait UsesBrokerMetricsAPIRoute {
  val brokerMetricsAPIRoute: BrokerMetricsAPIRoute
}

trait MixinBrokerMetricsAPIRoute {
  val brokerMetricsAPIRoute = BrokerMetricsAPIRoute
}
