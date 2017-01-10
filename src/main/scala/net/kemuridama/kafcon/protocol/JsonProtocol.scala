package net.kemuridama.kafcon.protocol

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.ISODateTimeFormat

trait JsonProtocol
  extends DefaultJsonProtocol
  with SprayJsonSupport {

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {

    private lazy val format = ISODateTimeFormat.dateTimeNoMillis

    def read(json: JsValue): DateTime = json match {
      case JsString(str) => format.parseDateTime(str)
      case _ => sys.error(s"Parse error: {$json}")
    }

    def write(datetime: DateTime) = JsString(format.print(datetime.withZone(DateTimeZone.UTC)))

  }

}
