package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Partition

trait PartitionJsonProtocol
  extends JsonProtocol
  with PartitionOffsetJsonProtocol {

  implicit val partitionFormat = jsonFormat5(Partition)

}
