package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.CombinedBrokerMetrics

trait CombinedBrokerMetricsJsonProtocol
  extends JsonProtocol
  with CombinedBrokerMetricsLogJsonProtocol {

  implicit val combinedBrokerMetricsFormat = jsonFormat2(CombinedBrokerMetrics)

}
