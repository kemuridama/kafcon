# Kafcon
Kafcon is a Apache Kafka controller powered by Scala.

Inspired by Kafka Manager and Trifecta which are famous as Kafka Web UI.

This is **EXPERIMENTAL** and **UNDER DEVELOPMENT** software for personal use.

## Features
The current beta version of Kafcon provides features below:

* RESTful API
  * Cluster metadata
  * Brokers metadata and metrics
  * Topics metadata
* Web user interface -> [Kafcon UI](https://github.com/kemuridama/kafcon-ui)

## RESTful API Endpoints
* Cluster API
  * `GET /api/v1/clusters`: Metadata of the cluster
* Broker API
  * `GET /api/v1/brokers`: Metadata of all brokers
  * `GET /api/v1/brokers/<brokerId>`: Metadata of a specific broker
* Broker metrics API
  * `GET /api/v1/brokers/metrics`: Metrics of all brokers
  * `GET /api/v1/brokers/metrics/combined`: Combined metrics of all brokers
  * `GET /api/v1/brokers/<brokerId>/metrics`: Metrics of a specific broker
* Topic API
  * `GET /api/v1/topics`: Metadata of all topics
  * `GET /api/v1/topics/<topicName>`: Metadata of a specific topic

## Future features

* Control multi Kafka clusters
* Control Kafka for cluster administrators
* Server down detection

## Licence

This software is licensed by MIT License, see LICENSE.
