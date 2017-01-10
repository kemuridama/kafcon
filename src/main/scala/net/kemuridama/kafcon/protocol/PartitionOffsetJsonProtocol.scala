package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.PartitionOffset

trait PartitionOffsetJsonProtocol extends JsonProtocol {

  implicit val partitionOffsetFormat = jsonFormat2(PartitionOffset)

}
