package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.ZooKeeperBroker

trait ZooKeeperBrokerJsonProtocol extends JsonProtocol {

  implicit val zookeeperBrokerFormat = jsonFormat6(ZooKeeperBroker)

}
