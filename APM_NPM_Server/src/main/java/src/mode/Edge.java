package src.mode;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Edge {

	private String source;
	
	@SerializedName("target")
	private String destination;
	private Map<String,Integer> metrics;
	private List<String> notices;
	private String showClass;
	
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
	public Map<String, Integer> getMetrics() {
		return metrics;
	}
	public void setMetrics(Map<String, Integer> metrics) {
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
	
}
