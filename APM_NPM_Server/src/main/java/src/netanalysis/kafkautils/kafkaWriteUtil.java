package src.netanalysis.kafkautils;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import src.globalinfo.KafkaInfo;

@Component
public class kafkaWriteUtil implements InitializingBean,AutoCloseable,DisposableBean{

	private static Logger log = Logger.getLogger(kafkaWriteUtil.class);

	private Producer<String, String> producer;

	private Properties props;
	
	@Autowired
	private KafkaInfo kafkaInfo;


	public void sendData(String data) {
		producer.send(new ProducerRecord<String, String>(kafkaInfo.getWriteTopic(), data), new Callback() {
			@Override
			public void onCompletion(RecordMetadata metaData, Exception exception) {

				if (exception != null) {
					exception.printStackTrace();
					log.error(exception.getMessage());
				} else {
					System.out.println("send kafka success");
					log.info("send kafka success");
				}
			}
		});
	}

	public void close() {
		producer.close();
	}

	@Override
	public void destroy() throws Exception {
		this.close();
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		props = new Properties();
		props.put("bootstrap.servers", kafkaInfo.getWriteAddr());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(props);
	}

}
