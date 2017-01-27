package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.{APIResponse, APIError}

trait APIResponseJsonProtocol
  extends JsonProtocol
  with APIErrorJsonProtocol {

  implicit def apiResponseFormat[T: JsonFormat] = new RootJsonFormat[APIResponse[T]] {

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      APIResponse(
        jsObject.getFields("data").headOption.map(_.convertTo[T]),
        jsObject.getFields("error").headOption.map(_.convertTo[APIError])
      )
    }

    def write(apiResponse: APIResponse[T]) = JsObject(
      "data" -> apiResponse.data.toJson,
      "error" -> apiResponse.error.toJson
    )

  }

}
