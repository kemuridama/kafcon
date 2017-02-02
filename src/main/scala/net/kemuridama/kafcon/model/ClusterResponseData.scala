package net.kemuridama.kafcon.model

case class ClusterResponseData(
  name: String,
  zookeepers: List[String],
  brokers: List[Broker],
  topics: List[Topic],
  partitionCount: Long,
  messageCount: Long,
  connectionState: ConnectionState
)
