package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class BrokerMetrics(
  messageInPerSec: MeterMetric,
  bytesInPerSec: MeterMetric,
  bytesOutPerSec: MeterMetric,
  system: SystemMetrics,
  created: DateTime = new DateTime
)
