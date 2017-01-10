package net.kemuridama.kafcon.model

case class MeterMetric(
  count: Long,
  meanRate: Double,
  oneMinuteRate: Double,
  fiveMinuteRate: Double,
  fifteenMinuteRate: Double
)
