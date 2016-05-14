package com.example.streaming;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.lucidworks.spark.util.SolrSupport;

import akka.event.Logging.LogLevel;

public class CarEventsProcessor {

	private CarEventsProcessor() {
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if (args.length < 4) {
			System.err
					.println("Usage: CarEventsProcessor <brokers> <topics> <zk_url> <index_name>\n"
							+ "  <brokers> is a list of one or more Kafka brokers\n"
							+ "  <topics> is a list of one or more kafka topics to consume from\n"
							+ " <zk_url> zookeeper url\n"
							+ " <index_name> name of solr index\n\n");
			System.exit(1);
		}

		String brokers = args[0];
		String topics = args[1];
		String zk_url = args[2];
		String index_name = args[3];

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DefaultScalaModule());
        
		// Create context with a 2 seconds batch interval
		SparkConf sparkConf = new SparkConf()
				.setAppName("CarEventsProcessor");
		sparkConf.setMaster("local[4]");
        
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(60));
		jssc.sparkContext().setLogLevel("ERROR");

		HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);

		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> messages = KafkaUtils
				.createDirectStream(jssc, String.class, String.class,
						StringDecoder.class, StringDecoder.class, kafkaParams,
						topicsSet);

		// Get the messages and extract payload 
		JavaDStream<String> events = messages
				.map(new Function<Tuple2<String, String>, String>() {
					@Override
					public String call(Tuple2<String, String> tuple2) {
						return tuple2._2();
					}
				});
		
		//convert to SolrDocuments
		JavaDStream<SolrInputDocument> parsedSolrEvents = events.map(incomingRecord -> EventParseUtil.convertData(incomingRecord));

		//send to solr
		SolrSupport.indexDStreamOfDocs(zk_url, index_name, 10, parsedSolrEvents.dstream());

		parsedSolrEvents.print();
		jssc.start();
		jssc.awaitTermination();
	}
}