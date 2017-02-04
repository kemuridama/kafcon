package net.kemuridama.kafcon.repository

import net.kemuridama.kafcon.model.Topic

trait TopicRepository {

  private var topics = List.empty[Topic]

  def insert(topic: Topic): Unit = {
    topics = topics.filterNot(t => t.name == topic.name && t.clusterId == topic.clusterId) :+ topic
  }

  def findAll(clusterId: Int): List[Topic] = topics.filter(_.clusterId == clusterId)

  def find(clusterId: Int, name: String): Option[Topic] = findAll(clusterId).find(_.name == name)

}

private[repository] object TopicRepository
  extends TopicRepository

trait UsesTopicRepository {
  val topicRepository: TopicRepository
}

trait MixinTopicRepository {
  val topicRepository = TopicRepository
}
