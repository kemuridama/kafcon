package net.kemuridama.kafcon.model

case class ZooKeeperPartition(
  version: Int,
  leader: Int,
  isr: List[Int],
  controller_epoch: Int,
  leader_epoch: Int
)
