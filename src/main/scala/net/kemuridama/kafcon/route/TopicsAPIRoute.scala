package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.APIResponse
import net.kemuridama.kafcon.protocol.TopicJsonProtocol
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}

trait TopicsAPIRoute
  extends APIRoute
  with UsesTopicService
  with TopicJsonProtocol {

  val route = pathPrefix("topics") {
    pathEnd {
      get {
        complete(APIResponse(Some(topicService.findAll(1))))
      }
    } ~
    pathPrefix(Segment) { name =>
      pathEnd {
        get {
          topicService.find(1, name) match {
            case Some(topic) => complete(APIResponse(Some(topic)))
            case _ => complete(StatusCodes.NotFound, errorMessage("Not found"))
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
