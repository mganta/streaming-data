An simple example to simulated messages from a connected car

Here is the message flow 

CarEventsProducer ==> kafka ==> SparkStreaming ==> Solr ==> Dahsboard

Pre-requisites:

1. Kafka
2. Solr Cloud
3. Spark  YARN/local
4. banana (optional for dashboards)

Steps:

1. Start zookeeper & Kafka
2. Start Solr
3. Create kafka topic
4. Start events producer
5. Start spark streaming
6. View records in Solr UI and/or banana

