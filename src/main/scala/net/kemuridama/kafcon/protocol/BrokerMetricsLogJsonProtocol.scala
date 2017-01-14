package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.BrokerMetricsLog

trait BrokerMetricsLogJsonProtocol
  extends JsonProtocol
  with MeterMetricsJsonProtocol
  with SystemMetricsJsonProtocol {

  implicit def brokerMetricsLogFormat = jsonFormat5(BrokerMetricsLog)

}
