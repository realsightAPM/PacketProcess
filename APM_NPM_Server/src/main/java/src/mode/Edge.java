package src.mode;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Edge {

	private String source;
	
	@SerializedName("target")
	private String destination;
	private Metadata metrics;
	private List<String> notices;
	
	@SerializedName("class")
	private String showClass ="normal";
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public Metadata getMetrics() {
		return metrics;
	}
	public void setMetrics(Metadata metrics) {
		this.metrics = metrics;
	}
	public List<String> getNotices() {
		return notices;
	}
	public void setNotices(List<String> notices) {
		this.notices = notices;
	}
	public String getShowClass() {
		return showClass;
	}
	public void setShowClass(String showClass) {
		this.showClass = showClass;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Edge){
			
		}
		return false;
	}
}
