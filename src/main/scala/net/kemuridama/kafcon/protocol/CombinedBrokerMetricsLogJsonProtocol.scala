package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.CombinedBrokerMetricsLog

trait CombinedBrokerMetricsLogJsonProtocol
  extends JsonProtocol
  with MeterMetricsJsonProtocol {

  implicit val combinedBrokerMetricsLogFormat = jsonFormat4(CombinedBrokerMetricsLog)

}
