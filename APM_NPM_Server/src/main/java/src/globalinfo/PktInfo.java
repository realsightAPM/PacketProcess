package src.globalinfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PktInfo {
	@Value("${color}")
	private String COLOR;
	
	@Value("${source_node_ip}")
	private 	String sourceNodeIP;
	
	@Value("${destination_node_ip}")
	private String destinationNodeIP;
	
	@Value("${source_node_name}")
	private 	String sourceNodeName;
	
	@Value("$(destinaton_node_Name)")
	private String destinationNodeName;
	
	@Value("${sniffTime}")
	private String sniffTime;
	
	@Value("${source_port}")
	private String sourcePort;
	
	@Value("${destination_port}")
	private String destinationPort;
	
	@Value("${source_ip}")
	private String sourceIP;
	
	@Value("${destination_ip}")
	private String destinationIP;
	
	@Value("${s_ip_port}")
	private String sIPPort;
	
	@Value("${d_ip_port}")
	private String dIPPort;
	
	@Value("${length}")
	private String length;

	@Value("${count}")
	private String count;

	@Value("${window_size}")
	private String windowSize;

	@Value("${expert_message}")
	private String expertMessage;
	
	public String getCOLOR() {
		return COLOR;
	}

	public void setCOLOR(String cOLOR) {
		COLOR = cOLOR;
	}

	public String getSourceNodeIP() {
		return sourceNodeIP;
	}

	public void setSourceNodeIP(String sourceNodeIP) {
		this.sourceNodeIP = sourceNodeIP;
	}

	public String getDestinationNodeIP() {
		return destinationNodeIP;
	}

	public void setDestinationNodeIP(String destinationNodeIP) {
		this.destinationNodeIP = destinationNodeIP;
	}

	public String getSniffTime() {
		return sniffTime;
	}

	public void setSniffTime(String sniffTime) {
		this.sniffTime = sniffTime;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	public String getsIPPort() {
		return sIPPort;
	}

	public void setsIPPort(String sIPPort) {
		this.sIPPort = sIPPort;
	}

	public String getdIPPort() {
		return dIPPort;
	}

	public void setdIPPort(String dIPPort) {
		this.dIPPort = dIPPort;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getSourceNodeName() {
		return sourceNodeName;
	}

	public void setSourceNodeName(String sourceNodeName) {
		this.sourceNodeName = sourceNodeName;
	}

	public String getDestinationNodeName() {
		return destinationNodeName;
	}

	public void setDestinationNodeName(String destinationNodeName) {
		this.destinationNodeName = destinationNodeName;
	}

	public String getWindowSize() { return windowSize; }

	public String getCount() { return  count; }

	public String getExpertMessage() { return expertMessage; }

}
