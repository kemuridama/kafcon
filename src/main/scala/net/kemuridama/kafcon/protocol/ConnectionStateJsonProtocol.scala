package net.kemuridama.kafcon.protocol

import spray.json._

import net.kemuridama.kafcon.model.ConnectionState

trait ConnectionStateJsonProtocol extends JsonProtocol {

  implicit object ConnectionStateFormat extends RootJsonFormat[ConnectionState] {
    def read(json: JsValue) = ConnectionState(json.convertTo[String])
    def write(state: ConnectionState) = state.value.toJson
  }

}
