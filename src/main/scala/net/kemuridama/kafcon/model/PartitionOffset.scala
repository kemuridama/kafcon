package net.kemuridama.kafcon.model

case class PartitionOffset(
  first: Long = 0L,
  last: Long = 0L
) {

  def getMessageCount: Long = last - first

}
