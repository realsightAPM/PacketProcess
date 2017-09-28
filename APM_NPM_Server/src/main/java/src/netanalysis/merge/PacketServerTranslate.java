package src.netanalysis.merge;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import src.globalinfo.PktInfo;
import src.globalinfo.ProtocolServer;
import src.globalinfo.ServerInfo;

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
		String protocol = pkt.get("protocol").getAsString();
		ServerInfo si = this.ps.getServerInfo(protocol);

		packetMapper(pkt, si.getSourceFieldMap(), PktInfo.SOURCE_SERVER_NAME.getValue(), si.getSourceServerName());
		packetMapper(pkt, si.getDestinationFieldMap(), PktInfo.DISTINATION_SERVER_NAME.getValue(), si.getDestinationServerName());
		return pkt;
	}

	private void packetMapper(JsonObject pkt, Map<String, String> map, String property, String value) {
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		map.forEach((k, v) -> {
			sb1.append(v);
			if (pkt.get(k) != null) {
				sb2.append(pkt.get(k).toString());
			}
		});
		if (sb1.toString().equals(sb2.toString())) {
			pkt.addProperty(property, value);
		} else {
			pkt.addProperty(property, "null");
		}
	}

}
