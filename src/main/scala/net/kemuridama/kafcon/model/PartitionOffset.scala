package net.kemuridama.kafcon.model

case class PartitionOffset(
  first: Long,
  last: Long
) {

  def getMessageCount: Long = last - first

}
