package net.kemuridama.kafcon.model

case class BrokerMetricsLogs(
  brokerId: Int,
  logs: List[Option[BrokerMetrics]]
)
