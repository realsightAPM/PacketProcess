package src.globalinfo;

public enum ResponseCode {
	SUCCESS(0),FAIL(1);
	
	private final int code;
	ResponseCode(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
}
