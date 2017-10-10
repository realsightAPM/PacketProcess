package src.globalinfo;

import java.util.Map;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class ServerApplication implements InitializingBean {
	
	private Map<String,String> serverToAppmap = Maps.newHashMap();
	
	@Value("${app_server_file}")
	private String app_server_file;
	
	public String getAppName(String serverName){
		return serverToAppmap.get(serverName);
	}
	
	public void init() throws DocumentException{
		this.serverToAppmap = ConfigLoad.loadAppServerFromXml(app_server_file);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
}
