package src.globalinfo;

import java.util.HashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProtocolServer implements InitializingBean{
	
	@Value("${protocol_server_file}")
	private String protocol_server_file;
	
	private HashMap<String,ServerInfo> map = new HashMap<>();
	
	private ProtocolServer ps;
	public HashMap<String, ServerInfo> getMap() {
		return map;
	}

	public void setMap(HashMap<String, ServerInfo> map) {
		this.map = map;
	}

	public ServerInfo getServerInfo(String protocol){
		return this.ps.getMap().get(protocol);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		this.ps = ConfigLoad.loadProtocolServerFromXml(protocol_server_file);
	}
	
}
