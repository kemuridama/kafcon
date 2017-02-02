package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.ClusterResponseData

trait ClusterResponseDataJsonProtocol
  extends JsonProtocol
  with BrokerJsonProtocol
  with TopicJsonProtocol
  with ConnectionStateJsonProtocol {

  implicit val clusterResponseDataFormat = jsonFormat7(ClusterResponseData)

}
