package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.APIError

trait APIErrorJsonProtocol
  extends JsonProtocol
  with APIErrorDetailJsonProtocol {

  implicit def apiErrorFormat = jsonFormat3(APIError)

}
