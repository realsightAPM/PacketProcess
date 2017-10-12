package src.globalinfo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

@Component
public class ProtocolServer implements InitializingBean{
	
	@Value("${protocol_server_file}")
	private String protocol_server_file;
	
	@Autowired
	private PktInfo pktInfo;
	
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
		String sourceIP = pktInfo.getSourceIP();
		//System.err.println("##### "+pkt.toString());
		String sourceserverIp = pkt.get(sourceIP).getAsString();
		String destinationServerIp = pkt.get(pktInfo.getDestinationIP()).getAsString();
		
		String sourceKey = sourceserverIp;
		String destinationKey = destinationServerIp;
		//log.info(sourceKey+" &&&&&&& "+destinationKey);
		Map<String,String> resultMap = Maps.newHashMap();
		String sourceServerName = this.map.getOrDefault(sourceKey, "client");
		String destinationServerName = this.map.getOrDefault(destinationKey, "client");
		//log.info(sourceServerName+"  $$$$$$$$  "+destinationServerName);
		resultMap.put(pktInfo.getSourceNodeName(), sourceServerName);
		resultMap.put(pktInfo.getDestinationNodeName(), destinationServerName );
		return resultMap;
	}
	
	public String getserverName(String key){
		return this.map.get(key);
	}
	
}
