package com.example.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class CarEventsProducer implements Runnable {

	private static Properties properties;
	private long sleepTime;
	private long startBackCounter;
	private int jumperSecs = 600;
	private int index;
	private int threadCount;
	private List<String[]> parsedMetaData;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final String[] vehicleEvents = { "Airbag Deployed", "Window Stuck", "Engine Overheat",
			"Windshield Wipers On", "Media Player On", "GPS On", "LowFuel light On", "GPS On", "FuelCap Loose",
			"Headlight Out", "Taillight Out", "Low Tire Pressure", "Seatbelt Off", "Handbrake On", "Cruise Control On",
			"Check Engine Light On", "Engine Service Soon Message" };

	public CarEventsProducer(Properties props, long sleepTime, List<String> metaData, long startBackDays, int index,
			int threadCount) {
		properties = props;
		this.sleepTime = sleepTime;
		this.index = index;
		this.startBackCounter = System.currentTimeMillis() - (startBackDays * 24 * 60 * 60 * 1000);
		this.threadCount = threadCount;
		this.parsedMetaData = parseAndLoadMetaData(metaData);
		System.out.println("creating instance " + index);
	}

	private List<String[]> parseAndLoadMetaData(List<String> metaData) {
		List<String[]> parsedMetaData = new ArrayList<String[]>();
		for (String inputLine : metaData) {
			String[] inputArray = inputLine.split(",");
			parsedMetaData.add(inputArray);
		}
		return parsedMetaData;
	}

	public void shutdown() {
		closed.set(true);
	}

	public void run() {
		Producer<String, String> producer = null;
		try {

			producer = new KafkaProducer<String, String>(properties);
			int counter = 0;
			int indexMin = (parsedMetaData.size() / threadCount) * index;
			System.out.println("current index " + index);
			int indexMax = indexMin + threadCount;
			int currentIndex = indexMin;
			while (true) {
				String outputLine = generateMessage(currentIndex);
				if (outputLine != null) {
					ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(
							properties.getProperty("producer_topic"), null, outputLine);
					producer.send(producerRecord);
					counter++;
					currentIndex = ThreadLocalRandom.current().nextInt(indexMin, indexMax+1);
					
					//if (currentIndex == indexMax + 1)
						//currentIndex = indexMin;
					System.out.println("kafka publish counter... from index " + index + " -> " + counter);
				}
				if (currentIndex == indexMin) {
					startBackCounter = startBackCounter + jumperSecs * 1000;
					try {
						Thread.sleep(sleepTime * ThreadLocalRandom.current().nextInt(1, 8));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			producer.close();
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 4) {
			System.out.println("Error: Missing argument properties_file sleep_interval or messages_file");
			System.out.println(
					"Usage: CarEventsProducer <producer properties file> <sleeptime_in_millis> <metadata_file> <start_back_days>");
		}

		InputStream in = new FileInputStream(new File(args[0]));
		Properties properties = new Properties();
		long sleepTime = Long.parseLong(args[1]);
		String metadataFile = args[2];
		long start_back_days = Long.parseLong(args[3]);
		if (in != null) {
			properties.load(in);
			in.close();

			List<String> metadata = CarEventsProducer.loadFile(metadataFile);

			int threadCount = 2;

			ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			for (int i = 0; i < threadCount; i++) {
				executorService.execute(
						new CarEventsProducer(properties, sleepTime, metadata, start_back_days, i, threadCount));
				System.out.println("created instance " + i);
			}
			executorService.shutdown();
		}
	}

	private String generateMessage(int CurrentIndex) {
		String[] messageInputs = parsedMetaData.get(CurrentIndex);
		Date date = new Date(startBackCounter);
		String updatedLine = messageInputs[0] + "," + messageInputs[1] + "," + messageInputs[2] + "," + messageInputs[3]
				+ "," + sdf.format(date) + "," + String.format("%d", ThreadLocalRandom.current().nextInt(0, 120)) + ","
				+ vehicleEvents[ThreadLocalRandom.current().nextInt(0, vehicleEvents.length)] + ","
				+ String.format("%.4f", ThreadLocalRandom.current().nextDouble(26, 34)) + ","
				+ String.format("%.4f", ThreadLocalRandom.current().nextDouble(-106, -94));
		return updatedLine;
	}

	private static List<String> loadFile(String metadataFile) {
		System.out.println("metadata file " + metadataFile);
		URI uri = new File(metadataFile).toURI();
		List<String> list = null;
		try (Stream<String> lines = Files.lines(Paths.get(uri))) {
			list = lines.collect(Collectors.toList());
		} catch (IOException e) {
			System.out.println("Failed to load file." + e);
		}
		return list;
	}
}
