package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.util.TwirlSupport
import net.kemuridama.kafcon.view.html._

object APIPageRoutes
  extends TwirlSupport {

  val route = pathPrefix("api") {
    pathEnd {
      get {
        complete(APIPageIndex.render)
      }
    }
  }

}
