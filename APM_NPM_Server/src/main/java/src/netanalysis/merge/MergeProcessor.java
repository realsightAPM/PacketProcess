package src.netanalysis.merge;


import com.google.gson.JsonObject;

public interface MergeProcessor {

	 JsonObject mergerPkt(JsonObject pkt, JsonObject statisticInfo);


}
