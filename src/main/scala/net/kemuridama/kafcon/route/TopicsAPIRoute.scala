package net.kemuridama.kafcon.route

import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, CreateTopicRequest}
import net.kemuridama.kafcon.protocol.{TopicJsonProtocol, CreateTopicRequestJsonProtocol}
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}

trait TopicsAPIRoute
  extends APIRoute
  with UsesTopicService
  with TopicJsonProtocol
  with CreateTopicRequestJsonProtocol {

  val route = pathPrefix("clusters" / IntNumber / "topics") { clusterId =>
    pathEnd {
      get {
        onSuccess(topicService.findAll(clusterId)) { response =>
          complete(APIResponse(Some(response)))
        }
      } ~
      post {
        entity(as[CreateTopicRequest]) { request =>
          onSuccess(topicService.create(clusterId, request.name, request.replicationFactor, request.partitionCount)) { response =>
            complete(StatusCodes.Created, APIResponse(Some(response)))
          }
        }
      }
    } ~
    pathPrefix(Segment) { name =>
      pathEnd {
        get {
          onSuccess(topicService.find(clusterId, name)) {
            case Some(response) => complete(APIResponse(Some(response)))
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
