package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.SystemMetrics

trait SystemMetricsJsonProtocol extends JsonProtocol {

  implicit val systemMetricsFormat = jsonFormat8(SystemMetrics)

}
