package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, APIError}
import net.kemuridama.kafcon.protocol.{APIResponseJsonProtocol, TopicJsonProtocol}
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}

trait TopicsAPIRoute
  extends UsesTopicService
  with APIResponseJsonProtocol
  with TopicJsonProtocol {

  val route = pathPrefix("topics") {
    pathEnd {
      get {
        complete(APIResponse(Some(topicService.getAll)))
      }
    } ~
    pathPrefix(Segment) { name =>
      pathEnd {
        get {
          topicService.get(name) match {
            case Some(topic) => complete(APIResponse(Some(topic)))
            case _ => complete(StatusCodes.NotFound, APIResponse[Unit](error = Some(APIError(message = Some("Not found")))))
          }
        }
      }
    }
  }

}

private[route] object TopicsAPIRoute
  extends TopicsAPIRoute
  with MixinTopicService

trait UsesTopicsAPIRoute {
  val topicsAPIRoute: TopicsAPIRoute
}

trait MixinTopicsAPIRoute {
  val topicsAPIRoute = TopicsAPIRoute
}
