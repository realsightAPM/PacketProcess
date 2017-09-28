package src.solrUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.StatsParams;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
@Scope("prototype")
public class SolrReaderUtil  implements InitializingBean,AutoCloseable,DisposableBean{

	/*
	 * 配置文件中的solr url
	 */
	@Value("${SOLR_URL}")
	private String SOLR_URL;
	
	/*
	 * 配置文件中的连接超时时间
	 */
	@Value("${SOLR_CONNECTION_TIME_OUT}")
	private int SOLR_CONNECTION_TIME_OUT;
	
	private static Log logger  = LogFactory.getLog(SolrReaderUtil.class); 
	private HttpSolrClient SOLR_Client;//solr查询的客户端
	private final int facet_rows = 10000;
	
	private int start =0;//记录查询读取到第几条记录了
	private Iterator<SolrDocument> solrDocIter = null;//普通查询的迭代器
	private Iterator<FacetField.Count> solrFacetIter = null;//facet查询的迭代器
	
	private String[] queryFields = null;//要查询获取哪些字段，返回的数据中包含的字段
	
	private String[] filters = null;//对查询出来的结果做哪些过滤，设置过滤条件
	
	private String[] statsFields = null;
	
	private String statsFacet = null;
	
	private String query ="*:*";//查询条件
	
	private String facetField;//facet的字段
	
	private int rows = 200;
	
	private String sortField = null;//排序的字段
	
	private boolean asc = false;
	
	private Map<String, FieldStatsInfo> fieldStatsInfo = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 * 该函数用于创建solr的客户端
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setConnectionTimeout(this.SOLR_CONNECTION_TIME_OUT);
		this.SOLR_Client.setParser(new XMLResponseParser());
	}
	
	private void updateResultList() throws SolrServerException{
		if (this.SOLR_Client == null) {
			logger.error("SOLR_Client can not be null.");
			throw new IllegalArgumentException(
					"SOLR_Client can not be null.");
		}
		SolrQuery params = new SolrQuery();
		params.setQuery(this.query);
		if (this.queryFields != null) {
			params.setFields(queryFields);
		}
		if (this.statsFields!=null && this.statsFields.length!=0) {
			params.set(StatsParams.STATS, true);
			params.set(StatsParams.STATS_FIELD, this.statsFields);
			if (this.statsFacet!=null && !this.statsFacet.equals("")) {
				params.set(StatsParams.STATS_FACET, this.statsFacet);
			}
		}
		
		params.setFilterQueries(filters);
		params.setRows(rows);
		params.setStart(start);
		if (this.facetField != null) {
			params.setFacet(true);
			params.setFacetLimit(facet_rows);
			params.addFacetField(this.facetField);
		}
		
		//System.err.println(SOLR_URL + " " + params.toQueryString());
		
		logger.info(SOLR_URL + " " + params.toQueryString());
		
		if (sortField != null) {
			params.setSort(sortField, this.asc?ORDER.asc:ORDER.desc);
		}
		try {
			QueryResponse response = SOLR_Client.query(params);
			SolrDocumentList solrDocList = response.getResults();
			List<FacetField> solr_facet_list = response.getFacetFields();
			this.fieldStatsInfo = response.getFieldStatsInfo();
			if (solr_facet_list!=null && solr_facet_list.size()>0) {
				this.solrFacetIter = solr_facet_list.get(0).getValues().iterator();
			}
			this.solrDocIter = solrDocList.iterator();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public SolrReaderUtil setQueryField(String[] queryFields){
		this.queryFields = queryFields;
		return this;
	}
	
	public SolrReaderUtil  setQueryRows(int rows){
		this.rows = rows;
		return this;
	}
	
	public SolrReaderUtil setQueryfacetField(String facetField){
		this.facetField = facetField;
		return this;
	}
	
	public SolrReaderUtil setQueryStatsFacet(String statsFacet){
		this.statsFacet = statsFacet;
		return this;
	}
	
	public SolrReaderUtil setQuerystatsFields(String[] statsFields){
		this.statsFields = statsFields;
		return this;
	}
	
	public SolrReaderUtil setQueryFilters(String[] filters){
		this.filters = filters;
		return this;
	}
	
	public SolrReaderUtil setQueryParam(String query){
		this.query = query;
		return this;
	}
	
	public Map<String, FieldStatsInfo> getStatsInfo(){
		return this.fieldStatsInfo;
	}
	
	public  boolean hasNextResponse() {
		if (solrDocIter!=null && solrDocIter.hasNext())
			return true;
		try {
			updateResultList();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		if (solrDocIter == null)
			return false;
		if (solrDocIter == null)
			return false;
		return solrDocIter.hasNext();
	}
	
	
	public  boolean hasNextFacet() {
		if (this.facetField == null)
			return false;
		if (solrFacetIter==null) {
			try {
				updateResultList();
			} catch (SolrServerException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		if (solrFacetIter == null)
			return false;
		return solrFacetIter.hasNext();
	}
	
	/*
	 * 对于facet查询，获取查询结果
	 */
	public JsonObject nextFacet() {
		if (hasNextFacet() == false)
			return null;
		FacetField.Count solr_facet = solrFacetIter.next();
		JsonObject json = new JsonObject();
		json.addProperty(solr_facet.getName(), solr_facet.getCount());
		return json;
	}

	/*
	 * 获取普通查询的下一条数据
	 */
	public  JsonObject nextResponse() {
		if (hasNextResponse() == false)
			return null;
		SolrDocument solr_doc = solrDocIter.next();
		JsonParser gjp = new JsonParser();
		Gson gson = new Gson();
		JsonObject json = gjp.parse(gson.toJson(solr_doc, SolrDocument.class)).getAsJsonObject();
		this.start += 1;
		return json;
	}

	/*
	 * 执行一次查询
	 */
	public void query() throws SolrServerException{
		updateResultList();
	}

	@Override
	public void close() throws Exception {
		this.SOLR_Client.close();
	}

	@Override
	public void destroy() throws Exception {
		this.close();
	}
}
