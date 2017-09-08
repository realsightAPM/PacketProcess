package apm.netanalysis.merge;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import apm.netanalysis.Utils.kafkaWriteUtil;

/*
 * 负责将数据定时发送给kafka
 */
public class PktSender  implements Runnable{

	/*
	 * 保存一秒内的统计信息
	 */
	private AtomicReference<HashMap<String,JsonObject>> mapAtomicf;
	
	private kafkaWriteUtil writer;
	
	public PktSender(AtomicReference<HashMap<String,JsonObject>> mapAtomicf,kafkaWriteUtil writer){
		this.mapAtomicf = mapAtomicf;
		this.writer = writer;
	}
	
	@Override
	public void run() {
		HashMap<String,JsonObject> olderMap = mapAtomicf.get();
		HashMap<String,JsonObject> newMap = new HashMap<String,JsonObject>();
		if( mapAtomicf.compareAndSet(olderMap, newMap) ){
			String data = getData(olderMap);
			writer.sendData(data);
		}
	}
	
	/*
	 * 将结果转换为字符串
	 */
	private String getData(HashMap<String,JsonObject> map){
		JsonArray result = new JsonArray();
		map.forEach((k,v)->{
			result.add(v);
		});
		return result.getAsString();
	}

}
