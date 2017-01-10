package net.kemuridama.kafcon.service

import org.quartz.impl.StdSchedulerFactory

trait SchedulerService {

  private val scheduler = StdSchedulerFactory.getDefaultScheduler

  def start = scheduler.start
  def shutdown = scheduler.shutdown

  def getScheduler = scheduler

}

private[service] object SchedulerService extends SchedulerService

trait UsesSchedulerService {
  val schedulerService: SchedulerService
}

trait MixinSchedulerService {
  val schedulerService = SchedulerService
}
