export _JAVA_OPTIONS="-Xmx2g"
java -cp car-streaming-data-0.1-jar-with-dependencies.jar com.example.streaming.CarEventsProducer events_producer.properties 2000 /Users/madhu/bdday/sample_input.txt 3