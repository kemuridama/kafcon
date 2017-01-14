package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class BrokerMetricsLog(
  messageInPerSec: MeterMetric,
  bytesInPerSec: MeterMetric,
  bytesOutPerSec: MeterMetric,
  system: SystemMetrics,
  created: DateTime = new DateTime
)
