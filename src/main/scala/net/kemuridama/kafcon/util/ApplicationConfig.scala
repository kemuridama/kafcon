package net.kemuridama.kafcon.util

import com.typesafe.config.ConfigFactory

trait ApplicationConfig {

  private lazy val config = ConfigFactory.load

  lazy val server = config.getConfig("server")
  lazy val cluster = config.getConfig("cluster")

}

private[util] object ApplicationConfig extends ApplicationConfig

trait UsesApplicationConfig {
  val applicationConfig: ApplicationConfig
}

trait MixinApplicationConfig {
  val applicationConfig = ApplicationConfig
}
