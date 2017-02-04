package net.kemuridama.kafcon.service

import javax.management._
import javax.management.remote._

import net.kemuridama.kafcon.model.Broker

trait MBeanServerConnectionService
  extends UsesBrokerService {

  private var connections = Map.empty[Int, MBeanServerConnection]

  def get(brokerId: Int): Option[MBeanServerConnection] = {
    connections.get(brokerId).orElse {
      for {
        broker <- brokerService.find(1, brokerId)
        jmxServiceUrl <- getJMXServiceURL(broker)
      } yield {
        val mbsc = JMXConnectorFactory.connect(jmxServiceUrl).getMBeanServerConnection
        connections += brokerId -> mbsc
        mbsc
      }
    }
  }

  private def getJMXServiceURL(broker: Broker): Option[JMXServiceURL] = {
    broker.jmxPort.map { jmxPort =>
      new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi".format(broker.host, jmxPort))
    }
  }

}

private[service] object MBeanServerConnectionService
  extends MBeanServerConnectionService
  with MixinBrokerService

trait UsesMBeanServerConnectionService {
  val mbeanServerConnectionService: MBeanServerConnectionService
}

trait MixinMBeanServerConnectionService {
  val mbeanServerConnectionService = MBeanServerConnectionService
}
