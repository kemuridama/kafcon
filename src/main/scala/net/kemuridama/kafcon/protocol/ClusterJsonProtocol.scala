package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Cluster

trait ClusterJsonProtocol
  extends JsonProtocol
  with BrokerJsonProtocol
  with TopicJsonProtocol {

  implicit val clusterFormat = jsonFormat6(Cluster)

}
