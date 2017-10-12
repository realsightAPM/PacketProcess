package src.netanalysis.merge;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import src.netanalysis.kafkautils.kafkaWriteUtil;


/*
 * 负责将数据定时发送给kafka
 */
@Component
public class PktSender implements Runnable ,DisposableBean{
	
	private static Logger log = Logger.getLogger(PktSender.class);

	/*
	 * 保存一秒内的统计信息
	 */
	private AtomicReference<HashMap<String, JsonObject>> mapAtomicf;

	@Autowired
	private kafkaWriteUtil writer;

	public void setMapAtomicf(AtomicReference<HashMap<String, JsonObject>> mapAtomicf){
		this.mapAtomicf = mapAtomicf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() 发送在一秒内数据合并的结果
	 */
	@Override
	public void run() {
		log.debug("send data");
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
			System.err.println(data);
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
	
	@Override
	public void destroy() throws Exception {
		this.writer.close();
	}

}
