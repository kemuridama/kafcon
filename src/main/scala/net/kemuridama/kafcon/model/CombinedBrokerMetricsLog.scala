package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class CombinedBrokerMetricsLog(
  messageInPerSec: MeterMetric = new MeterMetric,
  bytesInPerSec: MeterMetric = new MeterMetric,
  bytesOutPerSec: MeterMetric = new MeterMetric,
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

  def +(combinedMetricsLog: CombinedBrokerMetricsLog): CombinedBrokerMetricsLog = {
    CombinedBrokerMetricsLog(
      messageInPerSec + combinedMetricsLog.messageInPerSec,
      bytesInPerSec + combinedMetricsLog.bytesInPerSec,
      bytesOutPerSec + combinedMetricsLog.bytesOutPerSec,
      if (created.isBefore(combinedMetricsLog.created)) created else combinedMetricsLog.created
    )
  }

}
