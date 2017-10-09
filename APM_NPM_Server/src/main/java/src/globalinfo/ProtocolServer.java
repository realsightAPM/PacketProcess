package src.globalinfo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import src.graphutil.GraphUtil;

@Component
public class ProtocolServer implements InitializingBean{
	
	@Value("${protocol_server_file}")
	private String protocol_server_file;
	
	private Map<String,String> map = new HashMap<>();
	
	private Logger log = LoggerFactory.getLogger(ProtocolServer.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		map = ConfigLoad.loadProtocolServerFromXml(protocol_server_file);
		map.forEach((k,v)->{
			log.info("key  "+k+" value "+v);
		});
	}
	
	public Map<String,String> getServerName(JsonObject pkt){
		String sourceserverIp = pkt.get("source_ip").getAsString();
		String destinationServerIp = pkt.get("destination_ip").getAsString();
		String sourcePort = pkt.get("source_port").getAsString();
		String destinationPort = pkt.get("destination_port").getAsString();
		String sourceKey = sourceserverIp+":"+sourcePort;
		String destinationKey = destinationServerIp+":"+destinationPort;
		log.info(sourceKey+" &&&&&&& "+destinationKey);
		Map<String,String> resultMap = Maps.newHashMap();
		String sourceServerName = this.map.getOrDefault(sourceKey, "client");
		String destinationServerName = this.map.getOrDefault(destinationKey, "client");
		log.info(sourceServerName+"  $$$$$$$$  "+destinationServerName);
		resultMap.put(PktInfo.SOURCE_SERVER_NAME.getValue(), sourceServerName);
		resultMap.put(PktInfo.DESTINATION_SERVER_NAME.getValue(),destinationServerName );
		return resultMap;
	}
	
}
