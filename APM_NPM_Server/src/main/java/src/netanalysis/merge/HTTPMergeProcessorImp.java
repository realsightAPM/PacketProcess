package src.netanalysis.merge;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;

/*
 * 处理tcp协议的信息统计
 */
@Component
public class TCPMergeProcessorImp extends  MergeProcessor {

	private static Logger log = Logger.getLogger(TCPMergeProcessorImp.class);

	@Autowired
	public TCPMergeProcessorImp(PktInfo pktInfo){
		super(pktInfo);
	}
	
	@Override
	public JsonObject process(JsonObject pkt, JsonObject statisticInfo) {
		if (statisticInfo == null) {
			statisticInfo = new JsonObject();
			init(statisticInfo, pkt);
		} else {
			String count = pktInfo.getCount();
			Long count_no = statisticInfo.get(count).getAsLong();
			statisticInfo.addProperty(count,  count_no + 1);

			String length = pktInfo.getLength();
			statisticInfo.addProperty(length, statisticInfo.get(length).getAsLong() + pkt.get(length).getAsLong());

			String window_size = pktInfo.getWindowSize();
			Double new_window_size = (statisticInfo.get(window_size).getAsDouble() * count_no + pkt.get(window_size).getAsDouble()) / (count_no + 1);
			statisticInfo.addProperty(window_size, new_window_size);

			String window_size_value = pktInfo.getWindowSizeValue();
			Double new_window_size_value = (statisticInfo.get(window_size_value).getAsDouble() * count_no + pkt.get(window_size_value).getAsDouble()) / (count_no + 1);
			statisticInfo.addProperty(window_size_value, new_window_size_value);

			/*
			String window_size_scale = pktInfo.getWindowSize();
			Double new_window_size_scale = (statisticInfo.get(window_size_scale).getAsDouble() * count_no + pkt.get(window_size_scale).getAsDouble()) / (count_no + 1);
			statisticInfo.addProperty(window_size_scale, new_window_size_scale);
			*/
		}
		return statisticInfo;
	}

	@Override
	public String getSessionFingermark(JsonObject pkt) {
		String sourceIP = pkt.get(pktInfo.getSourceIP()).getAsString();
		String destinationIP = pkt.get(pktInfo.getDestinationIP()).getAsString();
		String sourcePort = pkt.get(pktInfo.getSourcePort()).getAsString();
		String destinationPort = pkt.get(pktInfo.getDestinationPort()).getAsString();
		String expertMessage = pkt.get(pktInfo.getExpertMessage()).getAsString();
		return sourceIP + ":" + sourcePort + ":" + destinationIP + ":" + destinationPort + ":" + expertMessage;
	}

	private void init(JsonObject statisticInfo, JsonObject pkt) {
		statisticInfo.addProperty(pktInfo.getCount(), 1);

		statisticInfo.addProperty(pktInfo.getSourceIP(), pkt.get(pktInfo.getSourceIP()).getAsString());
		statisticInfo.addProperty(pktInfo.getSourcePort(), pkt.get(pktInfo.getSourcePort()).getAsString());
		statisticInfo.addProperty(pktInfo.getDestinationIP(), pkt.get(pktInfo.getDestinationIP()).getAsString());
		statisticInfo.addProperty(pktInfo.getDestinationPort(), pkt.get(pktInfo.getDestinationPort()).getAsString());
		statisticInfo.addProperty(pktInfo.getExpertMessage(), pkt.get(pktInfo.getExpertMessage()).getAsString());

		statisticInfo.addProperty(pktInfo.getSniffTime(), pkt.get(pktInfo.getSniffTime()).getAsString());
		statisticInfo.addProperty(pktInfo.getLength(), pkt.get(pktInfo.getLength()).getAsLong());
		statisticInfo.addProperty(pktInfo.getWindowSize(), pkt.get(pktInfo.getWindowSize()).getAsDouble());
		statisticInfo.addProperty(pktInfo.getWindowSizeValue(), pkt.get(pktInfo.getWindowSizeValue()).getAsDouble());
		//statisticInfo.addProperty(pktInfo.getWindowSizeScalefactor(), pkt.get(pktInfo.getWindowSizeScalefactor()).getAsDouble());

		//jo.addProperty(pktInfo.getSOURCE_SERVER_NAME(), pkt.get(pktInfo.getSOURCE_SERVER_NAME()).getAsString());
		//jo.addProperty(pktInfo.getDESTINATION_SERVER_NAME(), pkt.get(pktInfo.getDESTINATION_SERVER_NAME()).getAsString());
	}
}
