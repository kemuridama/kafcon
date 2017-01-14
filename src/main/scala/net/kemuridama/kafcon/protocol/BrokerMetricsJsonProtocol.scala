package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.BrokerMetrics

trait BrokerMetricsJsonProtocol
  extends JsonProtocol
  with BrokerMetricsLogJsonProtocol {

  implicit def brokerMetricsFormat = jsonFormat2(BrokerMetrics)

}
