package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.APIResponse

trait APIResponseJsonProtocol extends JsonProtocol {

  implicit def apiResponseFormat[T: JsonFormat] = new RootJsonFormat[APIResponse[T]] {

    def read(json: JsValue) = json.asJsObject.getFields("data") match {
      case Seq(data) => APIResponse(
        data.convertTo[T]
      )
      case _ => sys.error(s"Mapping error: {$json}")
    }

    def write(apiResponse: APIResponse[T]) = JsObject(
      "data" -> apiResponse.data.toJson
    )

  }

}
