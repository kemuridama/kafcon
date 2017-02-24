package net.kemuridama.kafcon.model

case class CreateTopicRequest(
  name: String,
  replicationFactor: Int,
  partitionCount: Int
)
