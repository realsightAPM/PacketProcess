package src.netanalysis.merge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;
import src.globalinfo.ProtocolServer;

@Component
public class PacketMerge implements Runnable {

	/*
	 * 接收到的String应该是一个list列表 将String转换为一个object，然后取出其中的数据，进行统计
	 */
	private ConcurrentLinkedQueue<JsonArray> queue;
	private boolean shutdown;
	
	@Autowired
	protected PktInfo pktInfo;
	
	@Autowired
	private ProtocolServer ps;//转换得到server name
	
	/*
	 * 保存一秒内的统计信息
	 */
	private AtomicReference<HashMap<String, JsonObject>> mapAtomicf;

	private static Logger log = Logger.getLogger(PacketMerge.class);

	/*
	 * 保存对于不同应用或者协议的统计合并方式，map的key对应与mergerProcessor的名字，即包含在pkt中的一个ID号称作color
	 */
	private HashMap<String, MergeProcessor> megerProcessors = new HashMap<String, MergeProcessor>();

	public void init(ConcurrentLinkedQueue<JsonArray> queue,
			AtomicReference<HashMap<String, JsonObject>> mapAtomicf) {
		this.queue = queue;
		this.shutdown = false;
		this.mapAtomicf = mapAtomicf;
	}

	/*
	 * 做合并，将一个list的信息进行合并
	 */
	public void merge(JsonArray pktlist) {
		for (JsonElement pkt : pktlist) {
			JsonObject pktJO = pkt.getAsJsonObject();
			try {
				pktJO = packetToApplication(pktJO);
				add(pktJO);
			} catch (Exception e) {
				log.error("#######",e);
				e.printStackTrace();
			}
		}
	}

	/*
	 * 将一个数据包加入统计结果
	 */
	private void add(JsonObject pkt) {
		String color = pkt.get(pktInfo.getCOLOR()).getAsString();
		if (color == null) {
			log.error("color is null");
			return;
		}

		MergeProcessor processor = megerProcessors.get(color.toUpperCase());

		if (processor == null) {
			log.error("color  " + color + " no Mergeprocessor");
			return;
		}

		String key = processor.getSessionFingermark(pkt);

		Map<String, JsonObject> map = mapAtomicf.get();
		JsonObject statisticInfo = map.get(key);
		statisticInfo = processor.mergerPkt(pkt, statisticInfo);
		map.put(key, statisticInfo);
	}

	/*
	 * 获取将可以标示一个会话的字段拼成一个key,原服务和目的服务
	 */
	/*
	private String getSessionFingermark(JsonObject pkt) throws Exception {
		String sourceServerName = pkt.get(pktInfo.getSourceNodeName()).getAsString();
		String destinationServerName = pkt.get(pktInfo.getDestinationNodeName()).getAsString();
		return sourceServerName+destinationServerName;
	}
	*/

	public void close() {
		this.shutdown = true;
	}

	public void registerMergeProcessor(String color,MergeProcessor mp){
		this.megerProcessors.put(color, mp);
	}
	
	public void run() {
		while (!shutdown) {
			JsonArray pktInfoList;
			pktInfoList = queue.poll();
			if (pktInfoList != null) {
				merge(pktInfoList);
			}else{
				log.debug("pkt info list is null");
			}
		}
	}
	
	private JsonObject packetToApplication(JsonObject pkt) {
		Map<String,String> map = ps.getServerName(pkt);
		pkt.addProperty(this.pktInfo.getSourceNodeName(), map.get(this.pktInfo.getSourceNodeName()));
		pkt.addProperty(this.pktInfo.getDestinationNodeName(), map.get(this.pktInfo.getDestinationNodeName()));
		return pkt;
	}

}
