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
		 	String sniffTime = this.pktInfo.getSniffTime();
			String time = pkt.get(sniffTime).getAsString();
			System.err.println("sniffTime "+sniffTime+"  "+time);
			statisticInfo.addProperty(this.pktInfo.getSniffTime(),time );
	//		String destinationIPPort = pkt.get(this.pktInfo.getdIPPort()).getAsString();
		//	statisticInfo.addProperty(this.pktInfo.getdIPPort(), destinationIPPort);
		//	String sourceIPPort = pkt.get(this.pktInfo.getsIPPort()).getAsString();
			//statisticInfo.addProperty(this.pktInfo.getsIPPort(),sourceIPPort);
		 return statisticInfo;
	 }

	 abstract  JsonObject process(JsonObject pkt, JsonObject statisticInfo);
}
