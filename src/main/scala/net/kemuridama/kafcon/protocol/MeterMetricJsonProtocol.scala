package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.MeterMetric

trait MeterMetricsJsonProtocol extends JsonProtocol {

  implicit val meterMetricFormat = jsonFormat5(MeterMetric)

}
