package net.kemuridama.kafcon.model

import kafka.cluster.BrokerEndPoint
import org.joda.time.DateTime

case class Broker(
  id: Int,
  host: String,
  port: Int,
  jmxPort: Option[Int],
  timestamp: DateTime
) {

  def toBrokerEndPoint = BrokerEndPoint(id, host, port)

}
