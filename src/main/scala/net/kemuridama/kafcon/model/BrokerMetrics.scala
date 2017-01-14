package net.kemuridama.kafcon.model

case class BrokerMetrics(
  brokerId: Int,
  logs: List[Option[BrokerMetricsLog]]
)
