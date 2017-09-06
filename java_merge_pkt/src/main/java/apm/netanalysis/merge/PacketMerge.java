package apm.netanalysis.merge;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import apm.netanalysis.info.SessionFingermark;

public class PacketMerge implements Runnable{
   
	/*
	 * 接收到的String应该是一个list列表
	 * 将String转换为一个object，然后取出其中的数据，进行统计
	 */
	private ConcurrentLinkedQueue<JsonArray> queue;
	
	private boolean shutdown;
	
	private AtomicReference<HashMap<String,JsonObject>> mapAtomicf;
	
	/*
	 * 保存对于不同应用或者协议的统计合并方式，map的key对应与mergerProcessor的名字，即包含在pkt中的一个ID号称作color
	 */
	private HashMap<String,MergeProcessor> megerProcessors;
	
	private HashMap<String,SessionFingermark> sesionConfig;
	
	public  PacketMerge(ConcurrentLinkedQueue<JsonArray> queue){
		this.queue = queue;
		this.shutdown = false;
	}

	
	/*
	 * 做合并，将一个list的信息 
	 */
	public void Merger(List<JsonObject> list){
			for(JsonObject bi : list){
				String sessionFingermark = getSessionFingermark(bi);
				add(sessionFingermark,bi);
			}
	}
	
	/*
	 * 将一个数据包加入统计结果
	 */
	private void add(String key,JsonObject pkt){
		
	}
	
/*
 * 获取将可以标示一个会话的字段拼成一个key
 */
	private String getSessionFingermark(JsonObject bi){
		
		return "";
	}
	
	public void close(){
		this.shutdown = true;
	}


	public void run() {
		while(!shutdown){
			JsonArray pktInfoList = queue.poll();
			if(pktInfoList!=null){
				
			}
		}
		
	}
	
}
