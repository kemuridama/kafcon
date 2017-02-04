package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class BrokerMetricsLog(
  clusterId: Int,
  brokerId: Int,
  messageInPerSec: MeterMetric = new MeterMetric,
  bytesInPerSec: MeterMetric = new MeterMetric,
  bytesOutPerSec: MeterMetric = new MeterMetric,
  system: SystemMetrics = new SystemMetrics,
  created: DateTime = new DateTime
) {

  def +(metricsLog: BrokerMetricsLog): CombinedBrokerMetricsLog = {
    CombinedBrokerMetricsLog(
      messageInPerSec + metricsLog.messageInPerSec,
      bytesInPerSec + metricsLog.bytesInPerSec,
      bytesOutPerSec + metricsLog.bytesOutPerSec,
      if (created.isBefore(metricsLog.created)) created else metricsLog.created
    )
  }

}
