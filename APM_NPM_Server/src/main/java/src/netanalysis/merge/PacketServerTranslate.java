package src.netanalysis.merge;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;
import src.globalinfo.ProtocolServer;

@Component
public class PacketServerTranslate {

	@Autowired
	private ProtocolServer ps;

	public ProtocolServer getPs() {
		return ps;
	}

	public void setPs(ProtocolServer ps) {
		this.ps = ps;
	}

	/*
	 * 判断数据包属于哪一个应用,所属的源应用和目的应用
	 */
	public JsonObject packetToApplication(JsonObject pkt) {
		Map<String,String> map = ps.getServerName(pkt);
		pkt.addProperty(PktInfo.SOURCE_SERVER_NAME.getValue(), map.get(PktInfo.SOURCE_SERVER_NAME.getValue()));
		pkt.addProperty(PktInfo.DESTINATION_SERVER_NAME.getValue(), map.get(PktInfo.DESTINATION_SERVER_NAME.getValue()));
		return pkt;
	}
}
