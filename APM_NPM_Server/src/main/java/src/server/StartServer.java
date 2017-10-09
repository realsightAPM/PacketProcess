package src.server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import src.netanalysis.merge.MergeProcessor;
import src.netanalysis.merge.PacketMerge;
import src.netanalysis.merge.PktSender;
import src.netanalysis.merge.TCPMergeProcessorImp;
import src.netanalysis.producer.PacketProducer;

@Service
public class StartServer implements  InitializingBean,DisposableBean{
	private  ExecutorService packetProcessorPool = Executors.newFixedThreadPool(2);

	private ScheduledExecutorService dataSender = Executors.newScheduledThreadPool(3);

	@Autowired
	private  PacketProducer packetProducer;

	@Autowired
	private  PacketMerge packetMerge;

	@Autowired
	private PktSender ps;
	
	private  AtomicReference<HashMap<String, JsonObject>> mapAtomicf;

	public  void packetMergeStrat() {

		// init
		mapAtomicf = new AtomicReference<HashMap<String, JsonObject>>();
		mapAtomicf.set(new HashMap<String, JsonObject>());
		ConcurrentLinkedQueue<JsonArray> queue = new ConcurrentLinkedQueue<JsonArray>();
		packetProducer.setDataCache(queue);
		packetMerge.init(queue, mapAtomicf);

		//注册合并处理的processor
		MergeProcessor processor = new TCPMergeProcessorImp();
		packetMerge.registerMergeProcessor("TCP", processor);
		// execute
		packetProcessorPool.execute(packetProducer);
		packetProcessorPool.execute(packetMerge);
		
		ps.setMapAtomicf(mapAtomicf);
		dataSender.scheduleAtFixedRate(ps, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public  void close() {
		packetProducer.close();
		packetMerge.close();
		packetProcessorPool.shutdownNow();
		dataSender.shutdownNow();
	}
	
	@Override
	public void destroy() throws Exception {
		this.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		packetMergeStrat();
	}
}
