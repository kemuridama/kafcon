package net.kemuridama.kafcon.model

import kafka.cluster.BrokerEndPoint
import kafka.consumer.SimpleConsumer
import org.joda.time.DateTime

case class Broker(
  id: Int,
  clusterId: Int,
  host: String,
  port: Int,
  jmxPort: Option[Int],
  timestamp: DateTime
) {

  def toBrokerEndPoint = BrokerEndPoint(id, host, port)

  def withSimpleConsumer[T](func: SimpleConsumer => T): Option[T] = {
    try {
      val consumer = new SimpleConsumer(host, port, 3000, 65536, "kafcon-consumer")
      val ret = func(consumer)
      consumer.close
      Some(ret)
    } catch {
      case _: Throwable => None
    }
  }

}
