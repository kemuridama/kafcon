package net.kemuridama.kafcon.model

case class APIResponse[T](
  data: Option[T] = None,
  error: Option[APIError] = None
)
