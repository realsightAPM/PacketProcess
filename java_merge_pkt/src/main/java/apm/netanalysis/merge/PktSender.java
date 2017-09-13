package apm.netanalysis.merge;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import apm.netanalysis.Utils.kafkaWriteUtil;

/*
 * 负责将数据定时发送给kafka
 */
public class PktSender implements Runnable {

	/*
	 * 保存一秒内的统计信息
	 */
	private AtomicReference<HashMap<String, JsonObject>> mapAtomicf;

	private kafkaWriteUtil writer;

	public PktSender(AtomicReference<HashMap<String, JsonObject>> mapAtomicf, kafkaWriteUtil writer) {
		this.mapAtomicf = mapAtomicf;
		this.writer = writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() 发送在一秒内数据合并的结果
	 */
	@Override
	public void run() {
		HashMap<String, JsonObject> olderMap = mapAtomicf.get();
		if (olderMap.isEmpty()) {
			return;
		}
		HashMap<String, JsonObject> newMap = new HashMap<String, JsonObject>();
		if (mapAtomicf.compareAndSet(olderMap, newMap)) {
			if (olderMap.isEmpty()) {
				return;
			}
			String data = getData(olderMap);
			writer.sendData(data);
		}
	}

	/*
	 * 将结果转换为字符串
	 */
	private String getData(HashMap<String, JsonObject> map) {
		JsonArray result = new JsonArray();
		map.forEach((k, v) -> {
			result.add(v.getAsJsonObject());
		});
		return result.toString();
	}

	public void close() {
		this.writer.close();
	}

}
