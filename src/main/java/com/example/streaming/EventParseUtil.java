package com.example.streaming;

import org.apache.solr.common.SolrInputDocument;
import com.lucidworks.spark.util.SolrSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EventParseUtil {
	
    public EventParseUtil() {
		super();
	}
	

	 static SolrInputDocument convertData (String incomingRecord) throws NumberFormatException, ParseException {
			
		 String[] inputArray = incomingRecord.split(",");
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		 
		 EventDataElement eventDataElement = null;

			if (inputArray.length == 9) {
				eventDataElement = new EventDataElement(
						inputArray[0], inputArray[1], inputArray[2], Integer.valueOf(inputArray[3]).intValue(),
						sdf.parse(inputArray[4]),
						Integer.valueOf(inputArray[5]).intValue(),
						inputArray[6], inputArray[7], inputArray[8]);

			}
			
			SolrInputDocument solrDoc =
	                  SolrSupport.autoMapToSolrInputDoc(eventDataElement.getVin() + "_" + eventDataElement.getDateTime().getTime(), eventDataElement, null);
			
			return solrDoc;
	 }
}
