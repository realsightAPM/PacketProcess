package apm.netanalysis.merge;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import apm.netanalysis.mode.BaseInfo;
import apm.netanalysis.mode.BaseStatisticInfo;

public class PacketMerge implements Runnable{
	
	/*
	 * 存储数据包的统计信息
	 */
	private AtomicReference<HashMap<String,BaseStatisticInfo>> mapAtomicf ;
   
	/*
	 * 接收到的String应该是一个list列表
	 * 将String转换为一个object，然后取出其中的数据，进行统计
	 */
	private ConcurrentLinkedQueue<String> queue;
	
	private boolean shutdown;
	
	public  PacketMerge(ConcurrentLinkedQueue<String> queue, AtomicReference<HashMap<String,BaseStatisticInfo>> mapAtomicf){
		this.queue = queue;
		this.mapAtomicf = mapAtomicf; 
		this.shutdown = false;
	}

	
	/*
	 * 做合并，将一个list的信息 
	 */
	public void Merger(List<BaseInfo> list){
			for(BaseInfo bi : list){
				String sessionFingermark = getSessionFingermark(bi);
				add(sessionFingermark,bi);
			}
	}
	
	/*
	 * 
	 */
	private void add(String key,BaseInfo bi){
		final HashMap<String,BaseStatisticInfo> map = mapAtomicf.get();
		BaseStatisticInfo bsi = map.get(key);
		long length  = bsi.getByteCount() + bi.getLength();
		int count = bsi.getCount()+1;
		float rtt = bsi.getRtt();
		bsi.setByteCount(length);
		bsi.setCount(count);
		bsi.setRtt(rtt);
	}
	
	private String getSessionFingermark(BaseInfo bi){
		StringBuilder sb = new StringBuilder();
		sb.append(bi.getSourceApp());
		sb.append(bi.getDistinationApp());
		sb.append(bi.getProtocol());
		return sb.toString();
	}
	
	public void close(){
		this.shutdown = true;
	}


	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
