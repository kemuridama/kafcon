package net.kemuridama.kafcon.repository

import net.kemuridama.kafcon.model.Broker

trait BrokerRepository {

  private var brokers = List.empty[Broker]

  def insert(brokers: List[Broker]): Unit = {
    brokers.map { broker =>
      this.brokers = this.brokers.filterNot(b => b.id == broker.id && b.clusterId == broker.clusterId) :+ broker
    }
  }

  def findAll(clusterId: Int): List[Broker] = brokers.filter(_.clusterId == clusterId)

  def find(clusterId: Int, id: Int): Option[Broker] = findAll(clusterId).find(_.id == id)

}

private[repository] object BrokerRepository
  extends BrokerRepository

trait UsesBrokerRepository {
  val brokerRepository: BrokerRepository
}

trait MixinBrokerRepository {
  val brokerRepository = BrokerRepository
}
