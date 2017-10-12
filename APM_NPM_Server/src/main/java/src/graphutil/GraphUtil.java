package src.graphutil;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import src.globalinfo.PktInfo;
import src.globalinfo.ProtocolServer;
import src.globalinfo.ServerApplication;
import src.mode.Edge;
import src.mode.Graph;
import src.mode.Metadata;
import src.mode.Node;
import src.solrUtil.SolrReaderUtil;
import org.apache.solr.client.solrj.SolrServerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Service
public class GraphUtil implements InitializingBean{
	
	@Autowired
	private SolrReaderUtil solrReaderUtil;
	
	@Autowired
	private ProtocolServer ps;//转换得到server name
	
	@Autowired
	private ServerApplication sa;//根据server name 得到
	
	@Autowired
	private PktInfo pktInfo;

	/*
	@Value("${edgeSource}")
	private String edgeSource;//原端
	
	@Value("${edgeDestination}")
	private String edgeDestination;//目的端
	
	@Value("${sniffTime}")
	private String sniffTime;//抓取到数据包的时间
	
	
	@Autowired
	private JsonTranslate jsonTranslate;*/
	
	@Autowired
	private ServerApplication serverApplication;
	
	private Gson gson = new Gson();
	
	//设置查询出原服务和目的服务
	private String[] edgeSource_Destination;
	
	private Logger log = LoggerFactory.getLogger(GraphUtil.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.edgeSource_Destination = new String[2];
		this.edgeSource_Destination[0] = this.pktInfo.getSourceNodeIP();
		this.edgeSource_Destination[1] = this.pktInfo.getDestinationNodeIP();
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
		Set<String> nodeIdSet = getAllNodeId(this.pktInfo.getDestinationNodeIP(),queryFilters,this.edgeSource_Destination);
		
		List<Node> nodeList = new LinkedList<Node>();
		
		nodeIdSet.forEach((String nodeId)->{
			//把名称做转换，将IP:PORT转换成服务名称
		//	String serverName = ps.getserverName(nodeId);
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
			if(!json.isJsonNull() && !json.toString().equals("{}")){
				Edge edge = new Edge();
				String sourceNodeName = this.getServerName(json, this.pktInfo.getSourceNodeIP());
				String destinationNodeName = this.getServerName(json, this.pktInfo.getDestinationNodeIP());
				edge.setSource(sourceNodeName);
				edge.setDestination(destinationNodeName);
				edge.setMetrics(metadata);
				edgeList.add(edge);
			}
		});
		return edgeList;
	}

	
	public String  getGraphByTime(String startTime,String endTime) throws SolrServerException{
		List<Node> nodeList = getAllNodesByTime(startTime,endTime);
		List<Edge> edgeList = getAllEdgeByTime(startTime,endTime);
		Map<String,Node> appNodeMap = Maps.newHashMap();
		Map<String,Edge> appEdgeMap = Maps.newHashMap();
		
		//归类每个APP所属的server
		nodeList.forEach((node)->{
				String nodeName = node.getName();
				String appName = this.serverApplication.getAppName(nodeName);
				if(!appNodeMap.containsKey(appName))
				{
					Node newNode =  new Node();
					newNode.setDisplayName(appName);
					newNode.setName(appName);
					newNode.setRenderer("region");
					appNodeMap.put(appName,newNode);
				}
				Node graph = appNodeMap.get(appName);
				if(graph == null) System.err.println("graph is null");
				List<Node> list = graph.getNodes();
				if(list ==null) System.err.println("list is null");
				
				List<Node> nodesOfGraph = Optional.ofNullable(graph)
				.map(g->g.getNodes())
				.orElseThrow(()->new NullPointerException("GraphUtil 处理APP所属的server，出现空指针异常"));
				nodesOfGraph.add(node);
		});
		
		//处理所有的边
		Metadata metadata = new Metadata();
		metadata.setDanger(5);
		metadata.setNormal(100);
		metadata.setWaring(95);
		Set<String> edgeSet = Sets.newHashSet();
		
		edgeList.forEach((edge)->{
			String sourceServer = edge.getSource();
			String destinationServer = edge.getDestination();
			String sourceApp = this.sa.getAppName(sourceServer);
			String destinationApp = this.sa.getAppName(destinationServer);
			if(!edgeSet.add(sourceServer+destinationServer)){
				
			}
			if(sourceApp.equals(destinationApp)){
				Node graph = appNodeMap.get(sourceApp);
				List<Edge> edges = graph.getEdges();
				edges.add(edge);
			}else{
				Edge newedge = new Edge();
				newedge.setDestination(destinationApp);
				newedge.setSource(sourceApp);
				newedge.setMetrics(metadata);
				appEdgeMap.put(destinationApp+sourceApp, newedge);
			}
		});
		Graph bigGraph = new Graph();
		List<Edge> bigEdgelist = Lists.newLinkedList();
		appEdgeMap.forEach((k,edge)->{
			bigEdgelist.add(edge);
		});
		List<Node> bigNodelist = Lists.newLinkedList();
		appNodeMap.forEach((k,node)->{
			bigNodelist.add(node);
		});
		bigGraph.setConnections(bigEdgelist);
		bigGraph.setNodes(bigNodelist);
		String graphStr = this.gson.toJson(bigGraph);
		return graphStr;
	}
	
	/*
	 * 时间转换
	 */
	private String timeQuery(String startTime,String  endTime){
	/*	String startTimeStr =this.sniffTime+":"+ "["+timeUtil.formatUnixtime2(startTime)+" "+"TO"+
				" "+timeUtil.formatUnixtime2(endTime) +"]";*/
		String startTimeStr =this.pktInfo.getSniffTime()+":"+ "["+startTime+" "+"TO"+
				" "+endTime +"]";
		log.info(startTimeStr);
		return startTimeStr;
	}
	
	private String getServerName(JsonObject pkt,String key){
		System.err.println(pkt.toString());
		System.err.println("#### key ###"+key);
		String serverName = pkt.get(key).getAsString();
		return serverName;
	}
	

}
