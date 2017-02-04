package net.kemuridama.kafcon.model

case class Topic(
  name: String,
  clusterId: Int,
  brokers: List[Int],
  replicationFactor: Int,
  messageCount: Long,
  partitions: List[Partition]
)
