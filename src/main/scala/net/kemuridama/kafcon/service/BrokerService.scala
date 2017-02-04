package net.kemuridama.kafcon.service

import net.kemuridama.kafcon.model.Broker
import net.kemuridama.kafcon.repository.{UsesBrokerRepository, MixinBrokerRepository}

trait BrokerService
  extends UsesBrokerRepository
  with UsesClusterService {

  def update: Unit = {
    clusterService.find(1).map { cluster =>
      brokerRepository.insert(cluster.getAllBrokers)
    }
  }

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
