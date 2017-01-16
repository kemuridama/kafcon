package net.kemuridama.kafcon

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import net.kemuridama.kafcon.route._
import net.kemuridama.kafcon.protocol.APIResponseJsonProtocol
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

trait KafconServer
  extends UsesClustersAPIRoute
  with UsesBrokersAPIRoute
  with UsesBrokerMetricsAPIRoute
  with UsesTopicsAPIRoute
  with UsesApplicationConfig
  with APIResponseJsonProtocol {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executitonContext = system.dispatcher

  private val apiRoutes = pathPrefix("api" / "v1") {
    clustersAPIRoute.route ~
    brokersAPIRoute.route ~
    brokerMetricsAPIRoute.route ~
    topicsAPIRoute.route
  }

  private val routes = {
    APIPageRoutes.route ~
    apiRoutes
  }

  def start = {
    val listenAddress = applicationConfig.server.getString("listenAddress")
    val listenPort = applicationConfig.server.getInt("listenPort")
    Http().bindAndHandle(routes, listenAddress, listenPort)
    println(s"Kafcon server started at http://$listenAddress:$listenPort.")
  }

}

object KafconServer
  extends KafconServer
  with MixinClustersAPIRoute
  with MixinBrokersAPIRoute
  with MixinBrokerMetricsAPIRoute
  with MixinTopicsAPIRoute
  with MixinApplicationConfig

trait UsesKafconServer {
  val kafconServer: KafconServer
}

trait MixinKafconServer {
  val kafconServer = KafconServer
}
