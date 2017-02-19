package net.kemuridama.kafcon.service

import scala.concurrent.ExecutionContext.Implicits.global

import net.kemuridama.kafcon.model.Broker
import net.kemuridama.kafcon.repository.{UsesBrokerRepository, MixinBrokerRepository}

trait BrokerService
  extends UsesBrokerRepository
  with UsesClusterService {

  def update: Unit = {
    clusterService.all.foreach { clusters =>
      clusters.foreach { cluster =>
        cluster.getAllBrokers.foreach { brokers =>
          brokerRepository.insert(brokers)
        }
      }
    }
  }

  def all: List[Broker] = brokerRepository.all
  def find(clusterId: Int, id: Int): Option[Broker] = brokerRepository.find(clusterId, id)
  def findAll(clusterId: Int): List[Broker] = brokerRepository.findAll(clusterId)

}

object BrokerService
  extends BrokerService
  with MixinBrokerRepository
  with MixinClusterService

trait UsesBrokerService {
  val brokerService: BrokerService
}

trait MixinBrokerService {
  val brokerService = BrokerService
}
