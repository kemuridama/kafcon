package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Cluster

trait ClusterJsonProtocol
  extends JsonProtocol
  with BrokerJsonProtocol
  with TopicJsonProtocol
  with ConnectionStateJsonProtocol {

  implicit val clusterFormat = jsonFormat7(Cluster)

}
