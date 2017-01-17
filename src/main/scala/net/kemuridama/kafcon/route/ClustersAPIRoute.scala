package net.kemuridama.kafcon.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import net.kemuridama.kafcon.model.{APIResponse, Cluster}
import net.kemuridama.kafcon.service.{UsesZooKeeperService, MixinZooKeeperService}
import net.kemuridama.kafcon.service.{UsesBrokerService, MixinBrokerService}
import net.kemuridama.kafcon.service.{UsesTopicService, MixinTopicService}
import net.kemuridama.kafcon.util.{UsesApplicationConfig, MixinApplicationConfig}
import net.kemuridama.kafcon.protocol.{APIResponseJsonProtocol, ClusterJsonProtocol}

trait ClustersAPIRoute
  extends UsesZooKeeperService
  with UsesBrokerService
  with UsesTopicService
  with UsesApplicationConfig
  with APIResponseJsonProtocol
  with ClusterJsonProtocol {

  private lazy val clusterName = applicationConfig.cluster.getString("name")

  val route = pathPrefix("clusters") {
    pathEnd{
      get {
        complete(APIResponse(Cluster(
          clusterName,
          zookeeperService.getAll,
          brokerService.getAll,
          topicService.getAll,
          topicService.getAll.map(_.partitions.size).foldLeft(0L)((sum, partitionCount) => sum + partitionCount),
          topicService.getAll.map(_.messageCount).foldLeft(0L)((sum, messageCount) => sum + messageCount)
        )))
      }
    }
  }

}

private[route] object ClustersAPIRoute
  extends ClustersAPIRoute
  with MixinZooKeeperService
  with MixinBrokerService
  with MixinTopicService
  with MixinApplicationConfig

trait UsesClustersAPIRoute {
  val clustersAPIRoute: ClustersAPIRoute
}

trait MixinClustersAPIRoute {
  val clustersAPIRoute = ClustersAPIRoute
}
