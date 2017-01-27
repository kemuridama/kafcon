package net.kemuridama.kafcon.model

case class APIErrorDetail(
  domain: Option[String] = None,
  reason: Option[String] = None,
  message: Option[String] = None,
  location: Option[String] = None,
  locationType: Option[String] = None,
  extendedHelp: Option[String] = None,
  sendReport: Option[String] = None
)
