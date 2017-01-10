package net.kemuridama.kafcon.model

import javax.management._

sealed abstract class MetricsType(domain: String, properties: Map[String, String]) {

  def toObjectName: ObjectName = {
    val hashtable = new java.util.Hashtable[String, String]
    for ((key, value) <- properties) {
      hashtable.put(key, value)
    }
    new ObjectName(domain, hashtable)
  }

}

object MetricsType {

  case object OperatingSystem extends MetricsType("java.lang", Map("type" -> "OperatingSystem"))

  // Messages in rate
  case object MessagesInPerSec extends MetricsType("kafka.server", Map("type" -> "BrokerTopicMetrics", "name" -> "MessagesInPerSec"))

  // Byte in rate
  case object BytesInPerSec extends MetricsType("kafka.server", Map("type" -> "BrokerTopicMetrics", "name" -> "BytesInPerSec"))

  // Request rate
  case object ProduceRequestsPerSec extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestsPerSec", "request" -> "Produce"))
  case object FetchConsumerRequestsPerSec extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestsPerSec", "request" -> "FetchConsumer"))
  case object FetchFollowerRequestsPerSec extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestsPerSec", "request" -> "FetchFollower"))

  // Byte out rate
  case object BytesOutPerSec extends MetricsType("kafka.server", Map("type" -> "BrokerTopicMetrics", "name" -> "BytesOutPerSec"))

  // Log flush rate and time
  case object LogFlushRateAndTimeMs extends MetricsType("kafka.log", Map("Type" -> "LogFlushStats", "name" -> "LogFlushRateAndTimeMs"))

  // # of under replicated partitions (|ISR| < |all replicas|)
  case object UnderReplicatedPertitions extends MetricsType("kafka.server", Map("type" -> "ReplicaManager", "name" -> "UnderReplicatedPertitions"))

  // Is controller active on broker (only one broker in the cluster should have 1)
  case object ActiveControllerCount extends MetricsType("kafka.controller", Map("type" -> "KafkaController", "name" -> "ActiveControllerCount"))

  // Leader election rate (non-zero when there are broker failures)
  case object LeaderElectionRateAndTimeMs extends MetricsType("kafka.controller", Map("type" -> "ControllerStats", "name" -> "LeaderElectionsPerSec"))

  // Unclean leader election rate
  case object UncleanLeaderElectionsPerSec extends MetricsType("kafka.controller", Map("type" -> "ControllerStats", "name" -> "UncleanLeaderElectionsPerSec"))

  // Partition counts (mostly even across brokers)
  case object PertitionCount extends MetricsType("kafka.server", Map("type" -> "ReplicaManager", "name" -> "PartitionCount"))

  // Leader replica counts (mostly even across brokers)
  case object LeaderCount extends MetricsType("kafka.server", Map("type" -> "ReplicaManager", "name" -> "LeaderCount"))

  // ISR shrink rate
  case object ISRShrinksPerSec extends MetricsType("kafka.server", Map("type" -> "ReplicaManager", "name" -> "IsrShrinksPerSec"))

  // ISR expansion rate
  case object ISRExpandsPerSec extends MetricsType("kafka.server", Map("type" -> "ReplicaManager", "name" -> "IsrExpandsPerSec"))

  // Max lag in messages btw follower and leader replicas (lag should be proportional to the maximum batch size of a produce request)
  case object MaxLag extends MetricsType("kafka.server", Map("type" -> "ReplicaFetcherManager", "name" -> "MaxLag", "clientId" -> "Replica"))

  // Lag in messages per follower replica (lag should be propotyional to the maximum batch size of a produce request)
  case class ConsumerLag(clientId: String, topic: String, partition: String) extends MetricsType("kafka.server", Map("type" -> "FetcherLagMetrics", "name" -> "ConsumerLag", "clientId" -> clientId, "topic" -> topic, "partition" -> partition))

  // Requests waiting in the producer purgatory (non-zero if acks=-1 is used)
  case object ProducerPurgatorySize extends MetricsType("kafka.network", Map("type" -> "DelayedOperationPurgatory", "name" -> "PurgatorySize", "delayedOperation" -> "Produce"))

  // Requests waiting in the fetch purgatory (size depends on fetch.wait.max.ms in the consumer)
  case object FetchPurgatorySize extends MetricsType("kafka.server", Map("type" -> "DelayedOperationPurgatory", "name" -> "PurgatorySize", "delayedOperation" -> "Fetch"))

  // Request total time (broken into queue, local, remote and response send time)
  case object ProduceTotalTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "TotalTimeMs", "request" -> "Produce"))
  case object FetchConsumerTotalTimeMs extends MetricsType("kafka,network", Map("type" -> "RequestMetrics", "name" -> "TotalTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerTotalTimeMs extends MetricsType("kafka,network", Map("type" -> "RequestMetrics", "name" -> "TotalTimeMs", "request" -> "FetchFollower"))

  // Time the request waits in the request queue
  case object ProduceRequestQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestQueueTimeMs", "request" -> "Produce"))
  case object FetchConsumerRequestQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestQueueTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerRequestQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RequestQueueTimeMs", "request" -> "FetchFollower"))

  // Time the request is processed at the leader
  case object ProduceLocalTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "LocalTimeMs", "request" -> "Produce"))
  case object FetchConsumerLocalTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "LocalTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerLocalTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "LocalTimeMs", "request" -> "FetchFollower"))

  // Time the request waits for the follower (non-zero for produce requests when ack=-1)
  case object ProduceRemoteTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RemoteTimeMs", "request" -> "Produce"))
  case object FetchConsumerRemoteTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RemoteTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerRemoteTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "RemoteTimeMs", "request" -> "FetchFollower"))

  // Time the request waits in the response queue
  case object ProduceResponseQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseQueueTimeMs", "request" -> "Produce"))
  case object FetchConsumerResponseQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseQueueTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerResponseQueueTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseQueueTimeMs", "request" -> "FetchFollower"))

  // Time to send the response
  case object ProduceResponseSendTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseSendTimeMs", "request" -> "Produce"))
  case object FetchConsumerResponseSendTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseSendTimeMs", "request" -> "FetchConsumer"))
  case object FetchFollowerResponseSendTimeMs extends MetricsType("kafka.network", Map("type" -> "RequestMetrics", "name" -> "ResponseSendTimeMs", "request" -> "FetchFollower"))

  // Number of messages the consumer lags behind the producer by
  case class ConsumerMaxLag(clientId: String) extends MetricsType("kafka.consumer", Map("type" -> "ConsumerFetcherManager", "name" -> "MaxLag", "clientId" -> clientId))

  // The average fraction of time the network processors are idle (between 0 and 1, ideally > 0.3)
  case object NetworkProcessorAvgIdlePercent extends MetricsType("kafka.network", Map("type" -> "SocketServer", "name" -> "NetworkProcessorAvgIdlePercent"))

  // The average fraction of time the request handler threads are idle (between 0 and 1, ideally > 0.3)
  case object RequestHandlerAvgIdlePercent extends MetricsType("kafka.server", Map("type" -> "KafkaRequestHandlerPool", "name" -> "RequestHandlerAvgIdlePercent"))

}
