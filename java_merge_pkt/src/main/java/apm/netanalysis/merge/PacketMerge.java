package apm.netanalysis.merge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import apm.netanalysis.info.PktInfo;
import apm.netanalysis.info.SessionFingermark;

public class PacketMerge implements Runnable{
   
	/*
	 * 接收到的String应该是一个list列表
	 * 将String转换为一个object，然后取出其中的数据，进行统计
	 */
	private ConcurrentLinkedQueue<JsonArray> queue;
	
	private boolean shutdown;
	
	/*
	 * 保存一秒内的统计信息
	 */
	private AtomicReference<HashMap<String,JsonObject>> mapAtomicf;
	
	private static Logger log = Logger.getLogger(PacketMerge.class);
	
	/*
	 * 保存对于不同应用或者协议的统计合并方式，map的key对应与mergerProcessor的名字，即包含在pkt中的一个ID号称作color
	 */
	private HashMap<String,MergeProcessor> megerProcessors = new HashMap<String,MergeProcessor>();
	
	private HashMap<String,SessionFingermark> sesionConfig = new HashMap<String,SessionFingermark>();
	
	public  PacketMerge(ConcurrentLinkedQueue<JsonArray> queue){
		this.queue = queue;
		this.shutdown = false;
	}

	
	/*
	 * 做合并，将一个list的信息 
	 */
	public void merge(JsonArray pktlist){
			for(JsonElement pkt : pktlist){
			    JsonObject	pktJO = pkt.getAsJsonObject();
				String sessionFingermark;
				try {
					
					sessionFingermark = getSessionFingermark(pktJO);
					add(sessionFingermark,pktJO);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
	}
	
	/*
	 * 将一个数据包加入统计结果
	 */
	private void add(String key,JsonObject pkt){
		String color = pkt.get(PktInfo.COLOR.getValue()).toString();
		if(color  == null){
			log.error("color is null sessionkey is "+key);
		}
		MergeProcessor processor = megerProcessors.get(color);
		if(processor ==null){
			log.error("corol "+color+" no Mergeprocessor");
		}
		Map<String,JsonObject> map = mapAtomicf.get();
		JsonObject statisticInfo = map.get(key);
		statisticInfo = processor.mergerPkt(pkt, statisticInfo);
		map.put(key, statisticInfo);
	}
	
/*
 * 获取将可以标示一个会话的字段拼成一个key
 */
	private String getSessionFingermark(JsonObject pkt) throws Exception{
		String color = pkt.get(PktInfo.COLOR.getValue()).getAsString();
		SessionFingermark sfm = sesionConfig.get(color);
		StringBuilder sb = new StringBuilder();
		for(String field:sfm.getFiledsList()){
			if(pkt.get(field) == null){
				throw new Exception("the field of SessionFingermark not exits "+pkt.get(color));
			}else{
				sb.append(pkt.get(field));
			}
		}
		return sb.toString();
	}
	
	public void close(){
		this.shutdown = true;
	}


	public void run() {
		while(!shutdown){
			JsonArray pktInfoList = queue.poll();
			if(pktInfoList!=null){
				merge(pktInfoList);
			}
		}
	}
	
	public boolean register(String color,MergeProcessor processor,boolean cover){
		if(cover){
			megerProcessors.put(color, processor);
			return true;
		}else{
			if(megerProcessors.containsKey(color)) return false;
			megerProcessors.put(color, processor);
			return true;
		}
	}
	
}
