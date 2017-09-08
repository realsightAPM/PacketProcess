package apm.netanalysis.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import apm.netanalysis.info.KafkaInfo;

public class KafkaReadUtils {
 
	private Properties props;
	
	private KafkaConsumer<String,String> consumer;
	
	public void init(){
		props = new Properties();
		props.put("bootstrap.servers", KafkaInfo.getReadAddr());
	     props.put("group.id", "netanalysis");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     consumer = new KafkaConsumer<String,String>(props);
	     consumer.subscribe(Arrays.asList(KafkaInfo.getReadTopic()));
	}
	
	public List<String> getData(){
			List<String> list = new LinkedList<String>();
			ConsumerRecords<String,String> records = consumer.poll(100);
			System.out.println("$$$$$$$$");
			if(records==null || records.isEmpty())
				System.out.println("&&&&&& is NULL NULL NULL");
			
			for(ConsumerRecord<String, String> record : records){
				if(record!=null && record.value() !=null)
				{
					System.out.println("####"+record.value());
					list.add(record.value());
				}else{
					System.out.println(" null null");
				}
			}
		 return list;
	}
	
}
