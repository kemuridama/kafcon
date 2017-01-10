package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.ZooKeeperPartition

trait ZooKeeperPartitionJsonProtocol extends JsonProtocol {

  implicit val zookeeperPartitionFormat = jsonFormat5(ZooKeeperPartition)

}
