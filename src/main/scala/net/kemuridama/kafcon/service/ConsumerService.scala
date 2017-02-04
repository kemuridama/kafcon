package net.kemuridama.kafcon.service

import kafka.consumer.SimpleConsumer

trait ConsumerService
  extends UsesBrokerService {

  private var consumers = Map.empty[Int, SimpleConsumer]

  def get(brokerId: Int): Option[SimpleConsumer] = {
    consumers.get(brokerId).orElse {
      brokerService.find(1, brokerId).map { broker =>
        val consumer = new SimpleConsumer(broker.host, broker.port, 3000, 65536, "kafcon-consumer")
        consumers += brokerId -> consumer
        consumer
      }
    }
  }

}

private[service] object ConsumerService
  extends ConsumerService
  with MixinBrokerService

trait UsesConsumerService {
  val consumerService: ConsumerService
}

trait MixinConsumerService {
  val consumerService = ConsumerService
}
