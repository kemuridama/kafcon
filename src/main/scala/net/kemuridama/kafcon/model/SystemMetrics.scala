package net.kemuridama.kafcon.model

case class SystemMetrics(
  systemLoadAverage: Double,
  systemCpuLoad: Double,
  processCpuLoad: Double,
  totalPhysicalMemorySize: Long,
  freePhysicalMemorySize: Long,
  totalSwapSpaceSize: Long,
  freeSwapSpaceSize: Long,
  commitedVirtualMemorySize: Long
)
