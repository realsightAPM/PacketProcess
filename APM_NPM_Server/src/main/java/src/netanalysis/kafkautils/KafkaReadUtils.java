package src.netanalysis.kafkautils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import src.globalinfo.KafkaInfo;

@Component
public class KafkaReadUtils implements InitializingBean,AutoCloseable,DisposableBean{

	private Properties props;

	private KafkaConsumer<String, String> consumer;
	
	private static Logger log = LoggerFactory.getLogger(KafkaReadUtils.class);

	@Autowired
	private KafkaInfo kafkaInfo;
	
	public List<String> getData() {
		List<String> list = new LinkedList<String>();
		ConsumerRecords<String, String> records = consumer.poll(100);
		if (records == null || records.isEmpty())
		{
			log.debug("records is null");
			return list;
		}

		for (ConsumerRecord<String, String> record : records) {
			if (record != null && record.value() != null) {
				//log.info(record.value());
				list.add(record.value());
			}
		}
		return list;
	}

	@Override
	public void destroy() throws Exception {
		this.close();
	}

	@Override
	public void close() throws Exception {
		this.consumer.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		props = new Properties();
		props.put("bootstrap.servers", kafkaInfo.getReadAddr());
		props.put("group.id", "netanalysis");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(kafkaInfo.getReadTopic()));
	}

}
