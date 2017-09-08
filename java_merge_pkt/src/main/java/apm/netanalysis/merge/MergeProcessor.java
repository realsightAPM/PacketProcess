package apm.netanalysis.merge;


import java.util.List;

import com.google.gson.JsonObject;

public  abstract class MergeProcessor {

	private List<String> fileds;
	
	abstract	public JsonObject mergerPkt(JsonObject pkt,JsonObject statisticInfo);
	
	public List<String> getFileds() {
		return fileds;
	}
	public void setFileds(List<String> fileds) {
		this.fileds = fileds;
	}
	
	

}
