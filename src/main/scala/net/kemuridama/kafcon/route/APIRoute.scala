package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, APIError}
import net.kemuridama.kafcon.protocol.APIResponseJsonProtocol

trait APIRoute
  extends Directives
  with APIResponseJsonProtocol {

  def errorMessage(message: String): APIResponse[Unit] = {
    APIResponse[Unit](
      error = Some(APIError(message = Some(message)))
    )
  }

}
