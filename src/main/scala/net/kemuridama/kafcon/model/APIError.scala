package net.kemuridama.kafcon.model

case class APIError(
  errorCode: Option[Int] = None,
  message: Option[String] = None,
  errors: Seq[APIErrorDetail]
)
