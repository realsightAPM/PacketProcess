package src.netanalysis.merge;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;

@Component
public abstract class MergeProcessor {

	@Autowired
	private PktInfo pktInfo;
	
	 JsonObject mergerPkt(JsonObject pkt, JsonObject statisticInfo){
			String time = pkt.get(this.pktInfo.getSniffTime()).getAsString();
			statisticInfo.addProperty(this.pktInfo.getSniffTime(),time );
			String destinationIPPort = pkt.get(this.pktInfo.getdIPPort()).getAsString();
			statisticInfo.addProperty(this.pktInfo.getdIPPort(), destinationIPPort);
			String sourceIPPort = pkt.get(this.pktInfo.getsIPPort()).getAsString();
			statisticInfo.addProperty(this.pktInfo.getsIPPort(),sourceIPPort);
			statisticInfo = process(pkt,statisticInfo);
		 return statisticInfo;
	 }

	 abstract  JsonObject process(JsonObject pkt, JsonObject statisticInfo);
}
