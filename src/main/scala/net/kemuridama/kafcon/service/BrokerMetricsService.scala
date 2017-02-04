package net.kemuridama.kafcon.service

import javax.management._

import net.kemuridama.kafcon.model.{BrokerMetrics, CombinedBrokerMetrics, BrokerMetricsLog, CombinedBrokerMetricsLog, SystemMetrics, MeterMetric, MetricsType}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}

trait BrokerMetricsService
  extends UsesBrokerService
  with UsesApplicationConfig {

  import collection.JavaConversions._
  import collection.JavaConverters._

  private lazy val maxLogSize = applicationConfig.cluster.getInt("metricsMaxLogSize")

  private var metricsList = List.empty[BrokerMetrics]

  def update: Unit = {
    brokerService.findAll(1).foreach { broker =>
      val metricsLog = broker.withMBeanServerConnection { mbsc =>
        BrokerMetricsLog(
          messageInPerSec = getMeterMetric(mbsc, MetricsType.MessagesInPerSec.toObjectName),
          bytesInPerSec   = getMeterMetric(mbsc, MetricsType.BytesInPerSec.toObjectName),
          bytesOutPerSec  = getMeterMetric(mbsc, MetricsType.BytesOutPerSec.toObjectName),
          system          = getSystemMetrics(mbsc)
        )
      }

      metricsList = get(broker.id).fold(metricsList :+ BrokerMetrics(broker.id, metricsLog, List(metricsLog))) { metrics =>
        val logs = if (metrics.logs.size >= maxLogSize) metrics.logs.init else metrics.logs
        metricsList.filter(_.brokerId != broker.id) :+ metrics.copy(latest = metricsLog, logs = metricsLog +: logs)
      }
    }
  }

  def getAll: List[BrokerMetrics] = metricsList
  def get(brokerId: Int): Option[BrokerMetrics] = metricsList.find(_.brokerId == brokerId)

  def getCombined: CombinedBrokerMetrics = {
    val combinedMetricsLogs = metricsList.map(_.logs).transpose.map(_.flatten.foldLeft(new CombinedBrokerMetricsLog)((c, l) => c + l))
    CombinedBrokerMetrics(
      combinedMetricsLogs.head,
      combinedMetricsLogs
    )
  }

  private def getMeterMetric(mbsc: MBeanServerConnection, objectName: ObjectName): MeterMetric = {
    val attrList = Array("Count", "MeanRate", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate")
    val attrs = mbsc.getAttributes(objectName, attrList).asList.asScala.toList
    MeterMetric(
      getAttributeLongValue(attrs, "Count"),
      getAttributeDoubleValue(attrs, "MeanRate"),
      getAttributeDoubleValue(attrs, "OneMinuteRate"),
      getAttributeDoubleValue(attrs, "FiveMinuteRate"),
      getAttributeDoubleValue(attrs, "FifteenMinuteRate")
    )
  }

  private def getSystemMetrics(mbsc: MBeanServerConnection): SystemMetrics = {
    val attrList = Array("SystemLoadAverage", "SystemCpuLoad", "ProcessCpuLoad", "TotalPhysicalMemorySize", "FreePhysicalMemorySize", "TotalSwapSpaceSize", "FreeSwapSpaceSize", "CommittedVirtualMemorySize")
    val attrs = mbsc.getAttributes(MetricsType.OperatingSystem.toObjectName, attrList).asList.asScala.toList
    SystemMetrics(
      getAttributeDoubleValue(attrs, "SystemLoadAverage"),
      getAttributeDoubleValue(attrs, "SystemCpuLoad"),
      getAttributeDoubleValue(attrs, "ProcessCpuLoad"),
      getAttributeLongValue(attrs, "TotalPhysicalMemorySize"),
      getAttributeLongValue(attrs, "FreePhysicalMemorySize"),
      getAttributeLongValue(attrs, "TotalSwapSpaceSize"),
      getAttributeLongValue(attrs, "FreeSwapSpaceSize"),
      getAttributeLongValue(attrs, "CommittedVirtualMemorySize")
    )
  }

  private def getAttributeLongValue(attrs: List[Attribute], name: String): Long = attrs.find(_.getName == name).map(_.getValue.asInstanceOf[Long]).getOrElse(0L)
  private def getAttributeDoubleValue(attrs: List[Attribute], name: String): Double = attrs.find(_.getName == name).map(_.getValue.asInstanceOf[Double]).getOrElse(0D)

}

private[service] object BrokerMetricsService
  extends BrokerMetricsService
  with MixinBrokerService
  with MixinApplicationConfig

trait UsesBrokerMetricsService {
  val brokerMetricsService: BrokerMetricsService
}

trait MixinBrokerMetricsService {
  val brokerMetricsService = BrokerMetricsService
}
