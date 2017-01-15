package net.kemuridama.kafcon.model

case class MeterMetric(
  count: Long = 0L,
  meanRate: Double = 0D,
  oneMinuteRate: Double = 0D,
  fiveMinuteRate: Double = 0D,
  fifteenMinuteRate: Double = 0D
) {

  def +(metric: MeterMetric): MeterMetric = {
    MeterMetric(
      count + metric.count,
      meanRate + metric.meanRate,
      oneMinuteRate + metric.oneMinuteRate,
      fiveMinuteRate + metric.fiveMinuteRate,
      fifteenMinuteRate + metric.fifteenMinuteRate
    )
  }

}
