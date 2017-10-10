package src.netanalysis.merge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;

/*
 * 处理tcp协议的信息统计
 */
@Component
public class TCPMergeProcessorImp extends  MergeProcessor {

	@Autowired
	private PktInfo pktInfo;
	
	@Override
	public JsonObject process(JsonObject pkt, JsonObject statisticInfo) {
		if (statisticInfo == null) {
			statisticInfo = new JsonObject();
			init(statisticInfo, pkt);
		} else {
			long length1 =  statisticInfo.get("length").getAsLong();
			long length2 =  pkt.get("length").getAsLong();
			long bytes = length1 + length2;
			statisticInfo.addProperty("length", bytes);
		}
		return statisticInfo;
	}

	private void init(JsonObject jo, JsonObject pkt) {
		jo.addProperty("source_ip", pkt.get("source_ip").getAsString());
		jo.addProperty("source_port", pkt.get("source_port").getAsString());
		jo.addProperty("destination_ip", pkt.get("destination_ip").getAsString());
		jo.addProperty("destination_port", pkt.get("destination_port").getAsString());
		jo.addProperty("snifftime", pkt.get("snifftime").getAsString());
		jo.addProperty("length", pkt.get("length").getAsLong());
		jo.addProperty(pktInfo.getSOURCE_SERVER_NAME(), pkt.get(pktInfo.getSOURCE_SERVER_NAME()).getAsString());
		jo.addProperty(pktInfo.getDESTINATION_SERVER_NAME(), pkt.get(pktInfo.getDESTINATION_SERVER_NAME()).getAsString());
	}
}
