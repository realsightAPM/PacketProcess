import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import src.solrUtil.SolrReader;

public class GraphUtilTest {

	@Test
	public void testSolrReader(){
		SolrReader sr = new SolrReader();
		List<String> fqs = new LinkedList<String>();
		List<String> fieldList = new LinkedList<String>();
		//fieldList.add("source");
		fieldList.add("destination");
		sr.setQueryFields( fieldList.toArray(new String[fieldList.size()]));
		fqs.add("protocol:NBNS");
		sr.setFilters(fqs);
		sr.setFacet_field("source");
		System.out.println("testx");
		while(sr.hasNextResponse()){
			System.out.println("has next");
			System.out.println(sr.nextResponse());
		}
		/*
		System.out.println("@$@#$@#$#");
		Map<String,Long> map = sr.getFacetResult();
		map.forEach((k,v)->{
			System.out.println("key "+k+" value "+v);
		});
		*/
		
	}
}
