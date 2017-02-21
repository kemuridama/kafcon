package net.kemuridama.kafcon.repository

import scala.concurrent.Future

import net.kemuridama.kafcon.model.Topic

trait TopicRepository {

  private var topics = List.empty[Topic]

  def insert(topic: Topic): Future[Unit] = Future.successful {
    topics = topics.filterNot(t => t.name == topic.name && t.clusterId == topic.clusterId) :+ topic
  }

  def findAll(clusterId: Int): Future[List[Topic]] = Future.successful(topics.filter(_.clusterId == clusterId))

  def find(clusterId: Int, name: String): Future[Option[Topic]] = Future.successful(topics.find(c => c.clusterId == clusterId && c.name == name))

}

private[repository] object TopicRepository
  extends TopicRepository

trait UsesTopicRepository {
  val topicRepository: TopicRepository
}

trait MixinTopicRepository {
  val topicRepository = TopicRepository
}
