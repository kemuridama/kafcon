package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Broker

trait BrokerJsonProtocol extends JsonProtocol {

  implicit val brokerFormat = jsonFormat6(Broker)

}
