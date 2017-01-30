package net.kemuridama.kafcon.model

abstract class ConnectionState(val value: String)
object ConnectionState {

  case object Connected extends ConnectionState("connected")
  case object Disconnected extends ConnectionState("disconnected")

  def apply(value: String) = value match {
    case "connected" => Connected
    case "disconnected" => Disconnected
    case _ => sys.error("Mapping error")
  }

  def unapply(state: ConnectionState) = Some(state.value)

}
