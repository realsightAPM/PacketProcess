package apm.netanalysis.merge;


import com.google.gson.JsonObject;

public  interface MergeProcessor {
	
	void mergerPkt(JsonObject pkt,JsonObject statisticInfo);

}
