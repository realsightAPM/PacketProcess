package src.globalinfo;

import java.util.Map;

public class ServerInfo{
	private String protocol;
	private String sourceServerName;
	private Map<String,String> sourceFieldMap;
	private String destinationServerName;
	private Map<String,String> destinationFieldMap;
	
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSourceServerName() {
		return sourceServerName;
	}
	public void setSourceServerName(String sourceServerName) {
		this.sourceServerName = sourceServerName;
	}
	public Map<String, String> getSourceFieldMap() {
		return sourceFieldMap;
	}
	public void setSourceFieldMap(Map<String, String> sourceFieldMap) {
		this.sourceFieldMap = sourceFieldMap;
	}
	public String getDestinationServerName() {
		return destinationServerName;
	}
	public void setDestinationServerName(String destinationServerName) {
		this.destinationServerName = destinationServerName;
	}
	public Map<String, String> getDestinationFieldMap() {
		return destinationFieldMap;
	}
	public void setDestinationFieldMap(Map<String, String> destinationFieldMap) {
		this.destinationFieldMap = destinationFieldMap;
	}
	
}