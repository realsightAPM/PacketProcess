package apm.netanalysis.Utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import apm.netanalysis.info.KafkaInfo;

public class kafkaWriteUtil {

	private Producer<String,String> producer;
	
	private Properties props ;
	
	public void init(){
		props.put("bootstrap.servers", KafkaInfo.getWriteAddr());
			      
			      //Set acknowledgements for producer requests.      
			      props.put("acks", "all");
			      
			      //If the request fails, the producer can automatically retry,
			      props.put("retries", 0);
			      
			      //Specify buffer size in config
			      props.put("batch.size", 16384);
			      
			      //Reduce the no of requests less than 0   
			      props.put("linger.ms", 1);
			      
			      //The buffer.memory controls the total amount of memory available to the producer for buffering.   
			      props.put("buffer.memory", 33554432);
			      
			      props.put("key.serializer", 
			         "org.apache.kafka.common.serializa-tion.StringSerializer");
			         
			      props.put("value.serializer", 
			         "org.apache.kafka.common.serializa-tion.StringSerializer");
			      
			      producer = new KafkaProducer<String,String>(props);	       
	}
	
	public void sendData(String data){
		producer.send(new ProducerRecord<String,String>(
				KafkaInfo.getWriteTopic(),data
				));
	}
	
	public void close(){
		producer.close();
	}
	
}
