package apm.netanalysis.info;

public class KafkaInfo {
	private static String readAddr = "10.4.53.25:9092";
	private static String readTopic = "netpacket";
	private static String writeTopic = "netpacketstatistic";
	private static String writeAddr = "10.4.53.25:9092";

	public static String getReadAddr() {
		return readAddr;
	}

	public static void setReadAddr(String readAddr) {
		KafkaInfo.readAddr = readAddr;
	}

	public static String getWriteAddr() {
		return writeAddr;
	}

	public static void setWriteAddr(String writeAddr) {
		KafkaInfo.writeAddr = writeAddr;
	}

	public static void setReadTopic(String readTopic) {
		KafkaInfo.readTopic = readTopic;
	}

	public static void setWriteTopic(String writeTopic) {
		KafkaInfo.writeTopic = writeTopic;
	}

	public static String getReadTopic() {
		return readTopic;
	}

	public static String getWriteTopic() {
		return writeTopic;
	}

}
