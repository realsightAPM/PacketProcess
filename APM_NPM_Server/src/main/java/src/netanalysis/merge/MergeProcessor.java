package src.netanalysis.merge;

import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;

public abstract class MergeProcessor {

	protected PktInfo pktInfo;
	
	public MergeProcessor(PktInfo pktInfo){
		this.pktInfo = pktInfo;
	}
	
    JsonObject mergerPkt(JsonObject pkt, JsonObject statisticInfo){
		//System.err.println(pkt.toString());


		statisticInfo = process(pkt,statisticInfo);
		return statisticInfo;
    }

    abstract  JsonObject process(JsonObject pkt, JsonObject statisticInfo);

    abstract String getSessionFingermark(JsonObject pkt);
}
