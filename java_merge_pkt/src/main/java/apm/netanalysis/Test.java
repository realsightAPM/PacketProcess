package apm.netanalysis;



import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Test {

	public static void main(String[] args) {
		JsonParser parser = new JsonParser();
		JsonObject jo = parser.parse("{}").getAsJsonObject();
		if(jo.isJsonNull()){
			System.out.println("@@@@@@");
		}else{
			System.out.println("******");
		}
		//Son son = new Son();
		//son.print();
		/*Map<String,String> map = Maps.newHashMap();
	
		map.compute("aaa", (k,v)->(v==null)?"aaaa":"bbb");*/
		/*Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		 Producer<String, String> producer = new KafkaProducer<>(props);
		 for(int i = 0; i < 100; i++)
		 {
			 System.out.println("send ");
			 producer.send(new ProducerRecord<String,String>(
						KafkaInfo.getWriteTopic(),"test test"
						),new Callback(){
							@Override
							public void onCompletion(RecordMetadata metaData, Exception exception) {
								
									if(exception != null){
										exception.printStackTrace();
										//log.error(exception.getMessage());
									}else{
										System.out.println("send kafka success");
									//	log.info("send kafka success");
									}
							}
					
				});
		 }

		 producer.close();*/
		
	/*	JsonArray ja = new JsonArray();
		JsonObject jo =  new JsonObject();
		jo.addProperty("aaa", "bbb");
		JsonElement je = jo;
		ja.add(je);
		System.out.println(" $$$$ "+ja.toString());*/
	   /*  KafkaConsumer<String,String>    consumer = new KafkaConsumer<String,String>(props);
	     consumer.subscribe(Arrays.asList(KafkaInfo.getReadTopic()));
	     
	     List<String> list = new LinkedList<String>();
	    Map<String,List<PartitionInfo>>  map = consumer.listTopics();
	    
	    map.forEach((k,v)->{
	    	System.out.println("key "+k+"\n\r"+v.toString());
	    });
	    
	     System.out.println("start");
			ConsumerRecords<String,String> records = consumer.poll(Integer.MAX_VALUE);
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
			*/
	     
	}

}
