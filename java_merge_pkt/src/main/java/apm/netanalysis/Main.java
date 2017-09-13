package apm.netanalysis;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import apm.netanalysis.Utils.kafkaWriteUtil;

import apm.netanalysis.info.SessionFingermark;
import apm.netanalysis.merge.MergeProcessor;
import apm.netanalysis.merge.PacketMerge;
import apm.netanalysis.merge.PktSender;

import apm.netanalysis.merge.TestMergeProcessorImp;
import apm.netanalysis.producer.PacketProducer;

/*
 * 主函数
 */
public class Main {

	private static ExecutorService packetProcessorPool = Executors.newFixedThreadPool(2);

	private static ScheduledExecutorService dataSender = Executors.newScheduledThreadPool(3);

	private static PacketProducer packetProducer;

	private static PacketMerge packetMerge;

	private static AtomicReference<HashMap<String, JsonObject>> mapAtomicf;

	private static kafkaWriteUtil writer;

	public static void packetMergeStrat() {

		// init
		mapAtomicf = new AtomicReference<HashMap<String, JsonObject>>();
		mapAtomicf.set(new HashMap<String, JsonObject>());
		ConcurrentLinkedQueue<JsonArray> queue = new ConcurrentLinkedQueue<JsonArray>();
		packetProducer = new PacketProducer(queue);
		packetMerge = new PacketMerge(queue, mapAtomicf);

		MergeProcessor processor = new TestMergeProcessorImp();
		packetMerge.register("tcp", processor, true, new SessionFingermark());

		writer = new kafkaWriteUtil();

		// execute
		packetProcessorPool.execute(packetProducer);
		packetProcessorPool.execute(packetMerge);
		dataSender.scheduleAtFixedRate(new PktSender(mapAtomicf, writer), 0, 1000, TimeUnit.MILLISECONDS);
	}

	public static void close() {
		packetProducer.close();
		packetMerge.close();
		packetProcessorPool.shutdownNow();
		dataSender.shutdownNow();
		writer.close();
	}

	public static void main(String[] args) {
		packetMergeStrat();
	}

}
