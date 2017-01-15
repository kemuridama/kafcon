package net.kemuridama.kafcon.model

case class CombinedBrokerMetrics(
  latest: CombinedBrokerMetricsLog,
  logs: List[CombinedBrokerMetricsLog]
)
