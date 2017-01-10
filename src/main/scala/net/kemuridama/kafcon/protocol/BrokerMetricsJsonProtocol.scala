package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.BrokerMetrics

trait BrokerMetricsJsonProtocol
  extends JsonProtocol
  with MeterMetricsJsonProtocol
  with SystemMetricsJsonProtocol {

  implicit def brokerMetricsFormat = jsonFormat5(BrokerMetrics)

}
