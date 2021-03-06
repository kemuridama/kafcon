package net.kemuridama.kafcon.model

import org.joda.time.DateTime

case class ZooKeeperBroker(
  version: Int,
  host: String,
  port: Int,
  jmx_port: Option[Int],
  endpoints: List[String],
  timestamp: String
) {

  def toBroker(clusterId: Int, id: Int) = Broker(
    id = id,
    clusterId = clusterId,
    host = host,
    port = port,
    jmxPort = jmx_port,
    timestamp = new DateTime(timestamp.toLong)
  )

}
