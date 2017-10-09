package src.graphutil;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import src.mode.Edge;
import src.mode.Metadata;
import src.mode.Node;
import src.solrUtil.SolrReaderUtil;
import org.apache.solr.client.solrj.SolrServerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Service
public class GraphUtil implements InitializingBean{
	
	@Autowired
	private SolrReaderUtil solrReaderUtil;
	
	@Value("${edgeSource}")
	private String edgeSource;//原端
	
	@Value("${edgeDestination}")
	private String edgeDestination;//目的端
	
	@Value("${sniffTime}")
	private String sniffTime;//抓取到数据包的时间
	
	@Autowired
	private JsonTranslate jsonTranslate;
	
	private Gson gson = new Gson();
	
	private String[] edgeSource_Destination;
	
	private Logger log = LoggerFactory.getLogger(GraphUtil.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.edgeSource_Destination = new String[2];
		this.edgeSource_Destination[0] = edgeSource;
		this.edgeSource_Destination[1] = this.edgeDestination;
	}
	
	private Set<String> getAllNodeId(String facetField,String[] queryFilters,String[] fields) throws SolrServerException{
	/*	solrReaderUtil.setQueryFilters(queryFilters)
		.setQueryField(fields)
		.setQueryfacetField(facetField);*/
		solrReaderUtil
		.setQueryField(fields)
		.setQueryfacetField(facetField);
		
		try {
			solrReaderUtil.query();
		} catch (SolrServerException e) {
			log.error("查询拓扑图的节点",e);
			throw e;
		}
		Set<String> nodeIdSet = Sets.newHashSet();
		while(solrReaderUtil.hasNextFacet()){
			JsonObject response = solrReaderUtil.nextFacet();
			
			Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String,String> map = gson.fromJson(response.toString(), stringStringMap);
			map.forEach((k,v)->{
				log.info("key "+k);
				nodeIdSet.add(k);
			});
			
		}
		return nodeIdSet;
	}
	
	/*
	 * 获取所有节点,按时间段查询
	 */
	public  List<Node> getAllNodesByTime(String startTime,String endTime) throws SolrServerException{
		String[] queryFilters = new String[1];
		queryFilters[0] = this.timeQuery(startTime, endTime);
		Set<String> nodeIdSet = getAllNodeId(this.edgeDestination,queryFilters,this.edgeSource_Destination);
		
		List<Node> nodeList = new LinkedList<Node>();
		
		nodeIdSet.forEach((String nodeId)->{
			Node node = new Node();
			node.setDisplayName(nodeId);
			node.setName(nodeId);
			nodeList.add(node);
		});
		return nodeList;
	}
	
	/*
	 * 获取所有的边,指定时间段
	 */
	public List<Edge> getAllEdgeByTime(String startTime,String endTime) throws SolrServerException{
		List<Edge> edgeList = new LinkedList<Edge>();
		String[] queryFilter = new String[1];
		queryFilter[0] = timeQuery(startTime,endTime);
		//solrReaderUtil.setQueryFilters(queryFilter);
		solrReaderUtil.setQueryField(edgeSource_Destination);
		
		try {
			solrReaderUtil.query();
		} catch (SolrServerException e) {
				log.error("查询拓扑图的边错误", e);
				throw e;
		}
		List<JsonObject> queryResult = Lists.newLinkedList();
		while(this.solrReaderUtil.hasNextResponse()){
			queryResult.add(solrReaderUtil.nextResponse());
		}
		Metadata metadata = new Metadata();
		metadata.setDanger(5);
		metadata.setNormal(100);
		metadata.setWaring(95);
		queryResult.forEach((json)->{
			Edge edge = new Edge();
			edge.setSource(json.get(edgeSource).getAsString());
			edge.setDestination(json.get(edgeDestination).getAsString());
			edge.setMetrics(metadata);
			edgeList.add(edge);
		});
		return edgeList;
	}

	
	public String  getGraphByTime(String startTime,String endTime) throws SolrServerException{
		List<Node> nodeList = getAllNodesByTime(startTime,endTime);
		List<Edge> edgeList = getAllEdgeByTime(startTime,endTime);
		String graphStr = this.jsonTranslate.getGraphJson(edgeList, nodeList);
		return graphStr;
	}
	
	/*
	 * 时间转换
	 */
	private String timeQuery(String startTime,String  endTime){
	/*	String startTimeStr =this.sniffTime+":"+ "["+timeUtil.formatUnixtime2(startTime)+" "+"TO"+
				" "+timeUtil.formatUnixtime2(endTime) +"]";*/
		String startTimeStr =this.sniffTime+":"+ "["+startTime+" "+"TO"+
				" "+endTime +"]";
		log.info(startTimeStr);
		return startTimeStr;
	}
}
