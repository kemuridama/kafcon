package net.kemuridama.kafcon.model

case class Cluster(
  zookeepers: List[String],
  brokers: List[Broker],
  topics: List[Topic],
  partitionCount: Long,
  messageCount: Long
)
