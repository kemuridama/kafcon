package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.APIErrorDetail

trait APIErrorDetailJsonProtocol extends JsonProtocol {

  implicit def apiErrorDetailFormat = jsonFormat7(APIErrorDetail)

}
