package src.solrUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.StatsParams;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.qos.logback.classic.Logger;
import src.globalinfo.SolrInfo;

public class SolrReader{
	private final String SOLR_URL;
	private final HttpSolrClient SOLR_Client;
	private List<String> filters = null;
	private String[] queryFields = null;
	private String sortField = null;
	private String[] stats_fields = null;
	private String stats_facet = null;
	private String facet_field = null;
	private boolean asc = false; 
	private final int rows;
	private final int facet_rows = 10000;
	private int start = 0;
	private Iterator<SolrDocument> solr_doc_iter = null;
	private Iterator<FacetField.Count> solr_facet_iter = null;
	private Map<String, FieldStatsInfo> fieldStatsInfo = null;
	
	private Map<String,Long> facetResult = new HashMap<String,Long>();
	
	private static Logger logger = (Logger) LoggerFactory.getLogger(SolrReader.class);
	
	public SolrReader(){
		this.rows=100;
		this.SOLR_URL = SolrInfo.getUrl();
		SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setConnectionTimeout(1000 * 6);
	}
	
	public String getSOLR_URL() {
		return SOLR_URL;
	}
	
	public SolrReader(String SOLR_URL, int rows, String facet_fields, String stats_field, String stats_facet, List<String> filters) {
		if (SOLR_URL == null) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		if (rows <= 0) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		this.rows = rows;
		this.SOLR_URL = SolrInfo.getUrl();
		this.stats_fields = new String[] {stats_field};
		this.stats_facet = stats_facet;
		this.facet_field = facet_fields;
		this.filters = filters;
		this.SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setParser(new XMLResponseParser());
		this.SOLR_Client.setConnectionTimeout(1000 * 60 * 10);
	}
	
	public SolrReader(String SOLR_URL, int rows, String facet_field, List<String> stats_fields, String stats_facet, List<String> filters) {
		if (SOLR_URL == null) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		if (rows <= 0) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		this.rows = rows;
		this.SOLR_URL = SOLR_URL;
		this.stats_fields = new String[stats_fields.size()];
		for (int index = 0; index < stats_fields.size(); index++) {
			this.stats_fields[index] = stats_fields.get(index);
		}
		this.stats_facet = stats_facet;
		this.facet_field = facet_field;
		this.filters = filters;
		this.SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setParser(new XMLResponseParser());
		this.SOLR_Client.setConnectionTimeout(1000 * 60 * 10);
	}
	
	public SolrReader(String SOLR_URL, List<String> filters) {
		this(SOLR_URL, 10, null, new ArrayList<String>(), null, filters);
	}
	
	public SolrReader(String SOLR_URL, String facet_fields, List<String> filters) {
		this(SOLR_URL, 10, facet_fields, new ArrayList<String>(), null, filters);
	}
	
	private void updateResultList() throws SolrServerException{
		if (this.SOLR_Client == null) {
			logger.error("SOLR_Client can not be null.");
			throw new IllegalArgumentException(
					"SOLR_Client can not be null.");
		}
		SolrQuery params = new SolrQuery();
		params.setQuery("*:*");
		if (this.queryFields != null) {
			params.setFields(queryFields);
		}
		if (this.stats_fields!=null && this.stats_fields.length!=0) {
			params.set(StatsParams.STATS, true);
			params.set(StatsParams.STATS_FIELD, this.stats_fields);
			if (this.stats_facet!=null && !this.stats_facet.equals("")) {
				params.set(StatsParams.STATS_FACET, this.stats_facet);
			}
		}
		String[] filters = new String[this.filters.size()];
		for (int index = 0; index < this.filters.size(); index += 1) {
			filters[index] = this.filters.get(index);
		}
		params.setFilterQueries(filters);
		params.setRows(rows);
		params.setStart(start);
		if (this.facet_field != null) {
			params.setFacet(true);
			params.setFacetLimit(facet_rows);
			params.addFacetField(this.facet_field);
		}
		
		System.err.println(SOLR_URL + " " + params.toQueryString());
		
		logger.info(SOLR_URL + " " + params.toQueryString());
		
		if (sortField != null) {
			params.setSort(sortField, this.asc?ORDER.asc:ORDER.desc);
		}
		try {
			QueryResponse response = SOLR_Client.query(params);
			SolrDocumentList solr_doc_list = response.getResults();
			List<FacetField> solr_facet_list = response.getFacetFields();
			this.fieldStatsInfo = response.getFieldStatsInfo();
			if (solr_facet_list!=null && solr_facet_list.size()>0) {
				this.solr_facet_iter = solr_facet_list.get(0).getValues().iterator();
				while(solr_facet_iter.hasNext()){
					FacetField.Count solr_facet = solr_facet_iter.next();
					if(solr_facet == null) {
						System.out.println("solr_facet is null ");
						continue;
					}
					this.facetResult.put(solr_facet.getName(), solr_facet.getCount());
				}
			}
			this.solr_doc_iter = solr_doc_list.iterator();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Map<String, FieldStatsInfo> getFieldStatsInfo() {
		this.hasNextResponse();
		return this.fieldStatsInfo;
	}
	public void close() throws IOException {
		// TODO Auto-generated method stub
		this.SOLR_Client.close();
	}
	
	public void setSort(String sortField, boolean asc) {
		this.sortField = sortField;
		this.asc = asc;
	}
	
	public synchronized boolean hasNextFacet() {
		if (this.facet_field == null)
			return false;
		if (solr_facet_iter==null) {
			try {
				updateResultList();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		if (solr_facet_iter == null)
			return false;
		return solr_facet_iter.hasNext();
	}
	
	public synchronized String nextFacet() {
		if (hasNextFacet() == false)
			return null;
		FacetField.Count solr_facet = solr_facet_iter.next();
		JsonObject json = new JsonObject();
		json.addProperty(solr_facet.getName(), solr_facet.getCount());
		
		return json.toString();
	}
	
	public synchronized boolean hasNextResponse() {
		if (solr_doc_iter!=null && solr_doc_iter.hasNext())
			return true;
		try {
			updateResultList();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		if (solr_doc_iter == null)
			return false;
		if (solr_doc_iter == null)
			return false;
		return solr_doc_iter.hasNext();
	}
	
	public synchronized String nextResponse() {
		if (hasNextResponse() == false)
			return null;
		SolrDocument solr_doc = solr_doc_iter.next();
		JsonParser gjp = new JsonParser();
		Gson gson = new Gson();
		JsonObject json = gjp.parse(gson.toJson(solr_doc, SolrDocument.class)).getAsJsonObject();
		/*for (String name : solr_doc.getFieldNames()) {
			json.addProperty(name, solr_doc.getFirstValue(name).toString());
		}*/
		this.start += 1;
		return json.toString();
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public String[] getQueryFields() {
		return queryFields;
	}

	public void setQueryFields(String[] queryFields) {
		this.queryFields = queryFields;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String[] getStats_fields() {
		return stats_fields;
	}

	public void setStats_fields(String[] stats_fields) {
		this.stats_fields = stats_fields;
	}

	public String getStats_facet() {
		return stats_facet;
	}

	public void setStats_facet(String stats_facet) {
		this.stats_facet = stats_facet;
	}

	public String getFacet_field() {
		return facet_field;
	}

	public void setFacet_field(String facet_field) {
		this.facet_field = facet_field;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Iterator<SolrDocument> getSolr_doc_iter() {
		return solr_doc_iter;
	}

	public void setSolr_doc_iter(Iterator<SolrDocument> solr_doc_iter) {
		this.solr_doc_iter = solr_doc_iter;
	}

	public Iterator<FacetField.Count> getSolr_facet_iter() {
		return solr_facet_iter;
	}

	public void setSolr_facet_iter(Iterator<FacetField.Count> solr_facet_iter) {
		this.solr_facet_iter = solr_facet_iter;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		SolrReader.logger = logger;
	}

	public HttpSolrClient getSOLR_Client() {
		return SOLR_Client;
	}

	public int getRows() {
		return rows;
	}

	public int getFacet_rows() {
		return facet_rows;
	}

	public void setFieldStatsInfo(Map<String, FieldStatsInfo> fieldStatsInfo) {
		this.fieldStatsInfo = fieldStatsInfo;
	}
	
	public Map<String,Long> getFacetResult(){
		hasNextFacet();
		return this.facetResult;
	}
	
}
