solr-5.5.1/bin/solr create -c connectedCarData
curl -X POST http://localhost:8983/solr/connectedCarData/config -d '{"set-property":{"updateHandler.autoSoftCommit.maxTime":"2000"}}'