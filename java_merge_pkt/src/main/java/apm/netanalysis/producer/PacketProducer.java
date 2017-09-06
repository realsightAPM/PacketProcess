package apm.netanalysis.producer;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import apm.netanalysis.Utils.KafkaReadUtils;

public class PacketProducer implements Runnable{

	private ConcurrentLinkedQueue<String> dataCache;
	
	private KafkaReadUtils readKafka;
	
	private volatile boolean shutdown = false;
	
	public PacketProducer(ConcurrentLinkedQueue<String> dataCache){
		this.readKafka = new KafkaReadUtils();
		this.readKafka.init();
		this.dataCache = dataCache;
	}
	
	public void run() {
	
		while(shutdown){
			List<String> list = readKafka.getData();
			for(String str:list){
				
			}
		}
	}

	public void stop(){
		this.shutdown = true;
	}
}
