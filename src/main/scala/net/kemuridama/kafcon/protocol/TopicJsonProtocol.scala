package net.kemuridama.kafcon.protocol

import net.kemuridama.kafcon.model.Topic

trait TopicJsonProtocol
  extends JsonProtocol
  with PartitionJsonProtocol {

  implicit val topicFormat = jsonFormat6(Topic)

}
