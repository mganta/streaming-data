~/kafka/bin/kafka-topics.sh  --zookeeper localhost:2181 --delete --topic connectedCarsTopic
~/kafka/bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic connectedCarsTopic --partitions 4 --replication-factor 1
~/kafka/bin/kafka-topics.sh  --zookeeper localhost:2181 --list