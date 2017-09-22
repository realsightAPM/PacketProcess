package src.netanalysis.merge;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

/*
 * 处理tcp协议的信息统计
 */
@Component
public class TCPMergeProcessorImp extends MergeProcessor {

	@Override
	public JsonObject mergerPkt(JsonObject pkt, JsonObject statisticInfo) {
		if (statisticInfo == null) {
			statisticInfo = new JsonObject();
			init(statisticInfo, pkt);
		} else {
			statisticInfo.addProperty("rtt", pkt.get("rtt").getAsFloat());
			long bytes = statisticInfo.get("bytes").getAsLong() + pkt.get("bytes").getAsLong();
			statisticInfo.addProperty("bytes", bytes);
		}

		return statisticInfo;
	}

	private void init(JsonObject jo, JsonObject pkt) {
		jo.addProperty("source_ip", pkt.get("source_ip").toString());
		jo.addProperty("source_port", pkt.get("source_port").toString());
		jo.addProperty("bytes", pkt.get("bytes").getAsLong());
		jo.addProperty("rtt", pkt.get("rtt").getAsFloat());
		jo.addProperty("source_app", pkt.get("source_app").getAsString());
		jo.addProperty("distination_app", pkt.get("distination_app").getAsString());
	}
}
