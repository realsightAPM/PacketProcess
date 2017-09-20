package src.globalinfo;

/**
 * 存储solr的信息，例如访问哪一个solr，core name等
 *
 */
public class SolrInfo {
	private final static String URL = "http://10.4.53.25:8080/solr/nifi";
	private final static String CoreName = "nifi";

	public static String getUrl() {
		return URL;
	}

	public static String getCorename() {
		return CoreName;
	}

}
