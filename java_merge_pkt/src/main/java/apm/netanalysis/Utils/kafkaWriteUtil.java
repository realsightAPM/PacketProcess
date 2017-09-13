package apm.netanalysis.Utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import apm.netanalysis.info.KafkaInfo;

public class kafkaWriteUtil {

	private static Logger log = Logger.getLogger(kafkaWriteUtil.class);

	private Producer<String, String> producer;

	private Properties props;

	public kafkaWriteUtil() {
		init();
	}

	private void init() {
		props = new Properties();
		props.put("bootstrap.servers", KafkaInfo.getWriteAddr());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(props);
	}

	public void sendData(String data) {
		System.out.println("send data to kafka ((((((((((");
		producer.send(new ProducerRecord<String, String>(KafkaInfo.getWriteTopic(), data), new Callback() {
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
		System.out.println("###########");
	}

	public void close() {
		producer.close();
	}

}
