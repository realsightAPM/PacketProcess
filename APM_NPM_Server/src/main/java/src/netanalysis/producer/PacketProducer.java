package src.netanalysis.producer;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import src.netanalysis.kafkautils.KafkaReadUtils;


/*
 * 从kafka中读取数据，数据会是一个json array
 * 
 */
@Service
public class PacketProducer implements Runnable {

	private ConcurrentLinkedQueue<JsonArray> dataCache;

	private static Logger log = LoggerFactory.getLogger(PacketProducer.class);

	@Autowired
	private KafkaReadUtils readKafka;

	private volatile boolean shutdown = false;

	private JsonParser parser = new JsonParser();
	
	public ConcurrentLinkedQueue<JsonArray> getDataCache() {
		return dataCache;
	}

	public void setDataCache(ConcurrentLinkedQueue<JsonArray> dataCache) {
		this.dataCache = dataCache;
	}

	public void run() {
		while (!shutdown) {
			List<String> list = readKafka.getData();
			for (String str : list) {
				try {
					JsonArray pktList = parser.parse(str).getAsJsonArray();
					dataCache.add(pktList);
				} catch (Exception e) {
					log.error("read data from kafka is null", e);
				}
			}
		}
	}

	public void close() {
		this.shutdown = true;
	}
}
