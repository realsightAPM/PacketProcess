package apm.netanalysis.producer;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import apm.netanalysis.Utils.KafkaReadUtils;

/*
 * 从kafka中读取数据，数据会是一个json array
 * 
 */
public class PacketProducer implements Runnable {

	private ConcurrentLinkedQueue<JsonArray> dataCache;
	private KafkaReadUtils readKafka;

	private volatile boolean shutdown = false;

	private JsonParser parser = new JsonParser();

	public PacketProducer(ConcurrentLinkedQueue<JsonArray> dataCache) {
		this.readKafka = new KafkaReadUtils();
		this.readKafka.init();
		this.dataCache = dataCache;
	}

	public void run() {
		while (!shutdown) {
			List<String> list = readKafka.getData();
			for (String str : list) {
				try {
					JsonArray pktList = parser.parse(str).getAsJsonArray();
					System.out.println(pktList.toString());
					dataCache.add(pktList);
				} catch (Exception e) {
					System.out.println("get data is not json");
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		this.shutdown = true;
	}
}
