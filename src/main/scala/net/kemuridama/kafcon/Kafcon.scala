package net.kemuridama.kafcon

import org.quartz._

import net.kemuridama.kafcon.service._
import net.kemuridama.kafcon.util.MixinApplicationConfig

object kafcon
  extends App
  with MixinKafconServer
  with MixinClusterService
  with MixinSchedulerService
  with MixinApplicationConfig {

  lazy val updateInterval = applicationConfig.cluster.getInt("metricsMaxLogSize")

  // Setup scheduler for updating information and metrics
  clusterService.init
  val job = JobBuilder.newJob((new Job {
    def execute(context: JobExecutionContext) = {
      clusterService.update
    }
  }).getClass).withIdentity("updater", "kafcon").build
  val trigger = TriggerBuilder.newTrigger
    .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(updateInterval))
    .withIdentity("updaterTrigger").startNow.build
  schedulerService.getScheduler.scheduleJob(job, trigger)
  schedulerService.start

  // Start kafcon server
  kafconServer.start

}
