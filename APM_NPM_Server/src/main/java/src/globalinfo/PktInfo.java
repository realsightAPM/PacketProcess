package src.globalinfo;

public enum PktInfo {
	COLOR("protocol"),
	INFO("info"),
	SOURCE_SERVER_NAME("source_server_name"),
	DISTINATION_SERVER_NAME("destination_server_name");
	private final String value;
	
	PktInfo(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	
}
