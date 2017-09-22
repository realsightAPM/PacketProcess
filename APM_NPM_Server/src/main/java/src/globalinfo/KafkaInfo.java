package src.globalinfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaInfo {
	
	@Value("${kafka_read_addr}")
	private  String readAddr;
	
	@Value("${kafka_read_topic}")
	private String readTopic;
	
	@Value("${kafka_write_topic}")
	private  String writeTopic;
	
	@Value("${kafka_write_addr}")
	private String writeAddr;

	public   String getReadAddr() {
		return readAddr;
	}

	public   void setReadAddr(String readAddr) {
		this.readAddr = readAddr;
	}

	public   String getWriteAddr() {
		return writeAddr;
	}

	public   void setWriteAddr(String writeAddr) {
		this.writeAddr = writeAddr;
	}

	public   void setReadTopic(String readTopic) {
		this.readTopic = readTopic;
	}

	public   void setWriteTopic(String writeTopic) {
		this.writeTopic = writeTopic;
	}

	public   String getReadTopic() {
		return readTopic;
	}

	public   String getWriteTopic() {
		return writeTopic;
	}

}
