package src.globalinfo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.collect.Maps;

public class ConfigLoad {
	public static ProtocolServer loadProtocolServerFromXml(String fileName) throws DocumentException{
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		List<Element> childElements = root.elements();
		ProtocolServer ps = new ProtocolServer();
		for(Element child : childElements){
			String protocol = child.elementText("protocol_name");
			
			ServerInfo si = new ServerInfo();
			si.setProtocol(protocol);
			
			Element sourceFields = child.element("source_server");
			Element destinationFields = child.element("destination_server");
			String sourceServerName = sourceFields.attributeValue("name");
			String destinationName = destinationFields.attributeValue("name");
			
			List<Element> sourceFieldList = sourceFields.elements();
			List<Element> destinationList = destinationFields.elements();
			Map<String,String> sourceMap = listToMap(sourceFieldList);
			Map<String,String> destinationMap = listToMap(destinationList);
			
			si.setDestinationServerName(destinationName);
			si.setSourceServerName(sourceServerName);
			si.setDestinationFieldMap(destinationMap);
			si.setSourceFieldMap(sourceMap);
			ps.getMap().put(protocol, si);
		}
		return ps;
	}
	
	private static  Map<String,String> listToMap(List<Element> list){
		HashMap<String,String> map = Maps.newHashMap();
		list.forEach((field)->{
			String key = field.elementText("key");
			String value = field.elementText("value");
			map.put(key,value);
		});
		return map;
	}
}
