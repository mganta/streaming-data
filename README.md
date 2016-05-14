An simple example to simulate messages from a connected car

Here is the message flow 

CarEventsProducer ==> kafka ==> SparkStreaming ==> Solr ==> Dahsboard

Pre-requisites:

1. Kafka
2. Solr Cloud
3. Spark  YARN/local
4. banana (optional for dashboards)

Downloads:

Download dependent bits from and untar them into a folder

1. Spark  http://www.apache.org/dyn/closer.lua/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz
2. Solr   http://archive.apache.org/dist/lucene/solr/5.5.1/solr-5.5.1.tgz
3. Kafka  http://apache.cs.utah.edu/kafka/0.9.0.1/kafka_2.11-0.9.0.1.tgz
4. Banana https://github.com/lucidworks/banana

Steps:

1. Copy the scripts in src/main/resources into the folder where the software was extracted
2. Set the paths in the scripts accordingly
3. Start zookeeper & Kafka
4. Start Solr
5. Initialize solr
6. Create kafka topic
7. Start events producer
8. Start spark streaming
9. View records in Solr UI and/or banana


Notes:

1. To delete all records in solr collection
   curl http://localhost:8983/solr/connectedCarData/update -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>'
2. Enable kafka delete topics if you plan on deleting the topics
3. Copy the entire banana folder to solr-5.5.1/server/solr-webapp/webapp/ for dashboard
4. Your dashboard url will be http://localhost:8983/solr/banana/src/index.html





