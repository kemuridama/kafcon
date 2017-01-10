package net.kemuridama.kafcon

import org.quartz._

import net.kemuridama.kafcon.service.{MixinSchedulerService, MixinBrokerService, MixinBrokerMetricsService, MixinTopicService}
import net.kemuridama.kafcon.util.MixinApplicationConfig

object kafcon
  extends App
  with MixinKafconServer
  with MixinSchedulerService
  with MixinBrokerService
  with MixinBrokerMetricsService
  with MixinTopicService
  with MixinApplicationConfig {

  lazy val updateInterval = applicationConfig.cluster.getInt("metricsMaxLogSize")

  // Setup scheduler for updating information and metrics
  val job = JobBuilder.newJob((new Job {
    def execute(context: JobExecutionContext) = {
      brokerService.update
      brokerMetricsService.update
      topicService.update
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
