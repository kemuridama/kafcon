package net.kemuridama.kafcon.model

case class Partition(
  id: Int,
  leader: Option[Int],
  replicas: List[Int],
  isr: List[Int],
  firstOffset: Option[Long],
  lastOffset: Option[Long]
) {

  def getMessageCount: Long = {
    (for {
      first <- firstOffset
      last <- lastOffset
    } yield {
      last - first
    }).getOrElse(0L)
  }

}
