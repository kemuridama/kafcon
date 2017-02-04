package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Partition

trait PartitionJsonProtocol
  extends JsonProtocol {

  implicit val partitionFormat = jsonFormat6(Partition)

}
