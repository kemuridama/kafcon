package net.kemuridama.kafcon.model

import javax.management._
import javax.management.remote._

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

  def withMBeanServerConnection[T](func: MBeanServerConnection => T): Option[T] = {
    jmxPort.flatMap { port =>
      try {
        val jmxServiceUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi".format(host, port))
        val jmxConnector = JMXConnectorFactory.connect(jmxServiceUrl)
        val ret = func(jmxConnector.getMBeanServerConnection)
        jmxConnector.close
        Some(ret)
      } catch {
        case _: Throwable => None
      }
    }
  }

}
