package apm.netanalysis.mode;

/*
 * 从kafka读出来的数据包的基本信息
 */
public class BaseStatisticInfo {
	private String sourceIp;
	private String distinationIp;
	private long byteCount;
	private int count;//数据包个数
	private float rtt;
	
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getDistinationIp() {
		return distinationIp;
	}
	public void setDistinationIp(String distinationIp) {
		this.distinationIp = distinationIp;
	}
	public long getByteCount() {
		return byteCount;
	}
	public void setByteCount(long byteCount) {
		this.byteCount = byteCount;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public float getRtt() {
		return rtt;
	}
	public void setRtt(float rtt) {
		this.rtt = rtt;
	}
	
}
