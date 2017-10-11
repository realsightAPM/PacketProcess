package src.netanalysis.merge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;

/*
 * 处理tcp协议的信息统计
 */
@Component
public class TCPMergeProcessorImp extends  MergeProcessor {

	@Autowired
	public TCPMergeProcessorImp(PktInfo pktInfo){
		super(pktInfo);
	}
	
	@Override
	public JsonObject process(JsonObject pkt, JsonObject statisticInfo) {
		if (statisticInfo == null) {
			statisticInfo = new JsonObject();
			init(statisticInfo, pkt);
		} else {
			String length = pktInfo.getLength();
			JsonElement str = statisticInfo.get(length);
			long length1 =  str.getAsLong();
			long length2 =  pkt.get(pktInfo.getLength()).getAsLong();
			long bytes = length1 + length2;
			statisticInfo.addProperty(pktInfo.getLength(), bytes);
		}
		return statisticInfo;
	}

	private void init(JsonObject jo, JsonObject pkt) {
		jo.addProperty(pktInfo.getSourceIP(), pkt.get(pktInfo.getSourceIP()).getAsString());
		jo.addProperty(pktInfo.getSourcePort(), pkt.get(pktInfo.getSourcePort()).getAsString());
		jo.addProperty(pktInfo.getDestinationIP(), pkt.get(pktInfo.getDestinationIP()).getAsString());
		jo.addProperty(pktInfo.getDestinationPort(), pkt.get(pktInfo.getDestinationPort()).getAsString());
		jo.addProperty(pktInfo.getSniffTime(), pkt.get(pktInfo.getSniffTime()).getAsString());
		jo.addProperty(pktInfo.getLength(), pkt.get(pktInfo.getLength()).getAsLong());
		//jo.addProperty(pktInfo.getSOURCE_SERVER_NAME(), pkt.get(pktInfo.getSOURCE_SERVER_NAME()).getAsString());
		//jo.addProperty(pktInfo.getDESTINATION_SERVER_NAME(), pkt.get(pktInfo.getDESTINATION_SERVER_NAME()).getAsString());
	}
}
