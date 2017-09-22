package src.netanalysis.merge;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Component
public class TestMergeProcessorImp extends MergeProcessor {

	@Override
	public JsonObject mergerPkt(JsonObject pkt, JsonObject statisticInfo) {
		statisticInfo.addProperty("summary_line", pkt.get("summary_line").getAsString());
		statisticInfo.addProperty("protocol", pkt.get("protocol").getAsString());
		statisticInfo.addProperty("source", pkt.get("source").getAsString());
		statisticInfo.addProperty("destination", pkt.get("destination").getAsString());
		return statisticInfo;
	}

}
