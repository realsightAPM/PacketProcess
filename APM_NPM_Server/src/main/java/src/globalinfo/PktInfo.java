package src.globalinfo;

import org.springframework.beans.factory.annotation.Value;

public class PktInfo {
	@Value("${color}")
	private String COLOR;
	
	@Value("${source_server_name}")
	private 	String SOURCE_SERVER_NAME;
	
	@Value("$(destinaiton_server_name)")
	private String DESTINATION_SERVER_NAME;
	
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
	
	public String getCOLOR() {
		return COLOR;
	}

	public void setCOLOR(String cOLOR) {
		COLOR = cOLOR;
	}

	public String getSOURCE_SERVER_NAME() {
		return SOURCE_SERVER_NAME;
	}

	public void setSOURCE_SERVER_NAME(String sOURCE_SERVER_NAME) {
		SOURCE_SERVER_NAME = sOURCE_SERVER_NAME;
	}

	public String getDESTINATION_SERVER_NAME() {
		return DESTINATION_SERVER_NAME;
	}

	public void setDESTINATION_SERVER_NAME(String dESTINATION_SERVER_NAME) {
		DESTINATION_SERVER_NAME = dESTINATION_SERVER_NAME;
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

	
}
