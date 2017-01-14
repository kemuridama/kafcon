package net.kemuridama.kafcon.model

case class BrokerMetrics(
  brokerId: Int,
  latest: Option[BrokerMetricsLog],
  logs: List[Option[BrokerMetricsLog]]
)
