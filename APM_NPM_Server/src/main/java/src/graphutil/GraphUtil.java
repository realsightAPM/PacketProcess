package src.graphutil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import src.mode.Edge;
import src.mode.Metadata;
import src.mode.Node;
import src.solrUtil.SolrReaderUtil;
import src.solrUtil.TimeUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.assertj.core.util.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

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
	private TimeUtil timeUtil;
	
	@Autowired
	private JsonTranslate jsonTranslate;
	
	private String[] edgeSource_Destination;
	
	private Logger log = LoggerFactory.getLogger(GraphUtil.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.edgeSource_Destination = new String[2];
		this.edgeSource_Destination[0] = edgeSource;
		this.edgeSource_Destination[1] = this.edgeDestination;
	}
	
	private Set<String> getAllNodeId(String facetField,String[] queryFilters,String[] fields) throws SolrServerException{
		solrReaderUtil.setQueryFilters(queryFilters)
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
			nodeIdSet.add(response.get(this.edgeSource).getAsString());
		}
		return nodeIdSet;
	}
	
	/*
	 * 获取所有节点,按时间段查询
	 */
	public  List<Node> getAllNodesByTime(long startTime,long endTime) throws SolrServerException{
		String[] queryFilters = new String[1];
		queryFilters[0] = this.timeQuery(startTime, endTime);
		Set<String> nodeIdSet = getAllNodeId(this.edgeDestination,queryFilters,this.edgeSource_Destination);
		
		List<Node> nodeList = new LinkedList<Node>();
		Metadata metadata = new Metadata();
		nodeIdSet.forEach((String nodeId)->{
			Node node = new Node();
			node.setDisplayName(nodeId);
			node.setMetadate(metadata);
			nodeList.add(node);
		});
		return nodeList;
	}
	
	/*
	 * 获取所有的边,指定时间段
	 */
	public List<Edge> getAllEdgeByTime(long startTime,long endTime) throws SolrServerException{
		List<Edge> edgeList = new LinkedList<Edge>();
		String[] queryFilter = new String[1];
		queryFilter[0] = timeQuery(startTime,endTime);
		solrReaderUtil.setQueryFilters(queryFilter);
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
		queryResult.forEach((json)->{
			Edge edge = new Edge();
			edge.setSource(json.get(edgeSource).getAsString());
			edge.setDestination(json.get(edgeDestination).getAsString());
			edgeList.add(edge);
		});
		return edgeList;
	}

	
	public String  getGraphByTime(long startTime,long endTime) throws SolrServerException{
		List<Node> nodeList = getAllNodesByTime(startTime,endTime);
		List<Edge> edgeList = getAllEdgeByTime(startTime,endTime);
		String graphStr = this.jsonTranslate.getGraphJson(edgeList, nodeList);
		return graphStr;
	}
	
	/*
	 * 时间转换
	 */
	private String timeQuery(long startTime,long endTime){
		String startTimeStr =this.sniffTime+":"+ "["+timeUtil.formatUnixtime2(startTime)+" "+"TO"+
				" "+timeUtil.formatUnixtime2(endTime) +"]";
		return startTimeStr;
	}
}
