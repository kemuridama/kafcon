val zookeeperVersion = "3.4.9"
val kafkaVersion = "0.10.1.1"
val akkaHttpVersion = "10.0.1"
val twirlVersion = "1.3.0"

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    scalaVersion := "2.11.8",
    version := "0.0.1",
    name := "kafcon",
    mainClass in assembly := Some("net.kemuridama.kafcon.Kafcon"),
    libraryDependencies := Seq(
      "org.apache.zookeeper" % "zookeeper" % zookeeperVersion,
      "org.apache.kafka" %% "kafka" % kafkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "joda-time" % "joda-time" % "2.9.7",
      "org.joda" % "joda-convert" % "1.8.1",
      "org.quartz-scheduler" % "quartz" % "2.2.3",
      "com.typesafe.play" %% "twirl-api" % twirlVersion
    )
  )
