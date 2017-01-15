package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class BrokerMetricsLog(
  messageInPerSec: MeterMetric = new MeterMetric,
  bytesInPerSec: MeterMetric = new MeterMetric,
  bytesOutPerSec: MeterMetric = new MeterMetric,
  system: SystemMetrics = new SystemMetrics,
  created: DateTime = new DateTime
)
