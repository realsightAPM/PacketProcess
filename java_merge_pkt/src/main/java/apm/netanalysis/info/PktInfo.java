package apm.netanalysis.info;

public enum PktInfo {
	COLOR("color"),
	INFO("info");
	private final String value;
	
	PktInfo(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	
}
