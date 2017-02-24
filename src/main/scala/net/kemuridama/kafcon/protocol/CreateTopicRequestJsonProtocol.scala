package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.CreateTopicRequest

trait CreateTopicRequestJsonProtocol
  extends JsonProtocol {

  implicit def createTopicRequestFormat = jsonFormat3(CreateTopicRequest)

}
