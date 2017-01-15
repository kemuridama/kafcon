package net.kemuridama.kafcon.model

case class SystemMetrics(
  systemLoadAverage: Double = 0D,
  systemCpuLoad: Double = 0D,
  processCpuLoad: Double = 0D,
  totalPhysicalMemorySize: Long = 0L,
  freePhysicalMemorySize: Long = 0L,
  totalSwapSpaceSize: Long = 0L,
  freeSwapSpaceSize: Long = 0L,
  commitedVirtualMemorySize: Long = 0L
)
