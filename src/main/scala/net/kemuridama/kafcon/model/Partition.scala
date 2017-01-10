package net.kemuridama.kafcon.model

case class Partition(
  id: Int,
  leader: Option[Int],
  replicas: List[Int],
  isr: List[Int],
  offset: Option[PartitionOffset]
) {

  def getMessageCount: Long = offset.map(_.getMessageCount).getOrElse(0L)

}
