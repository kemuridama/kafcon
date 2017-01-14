package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.BrokerMetricsLogs

trait BrokerMetricsLogsJsonProtocol
  extends JsonProtocol
  with BrokerMetricsLogJsonProtocol {

  implicit def brokerMetricsLogsFormat = jsonFormat2(BrokerMetricsLogs)

}
