package src.graphutil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import src.controller.GraphController;
import src.mode.Edge;
import src.mode.Graph;
import src.mode.Node;

@Service
public class JsonTranslate {
	
	private Gson gson = new Gson();
	
	private static Logger log = LoggerFactory.getLogger(GraphController.class);
	
	public String getGraphJson(List<Edge> edgeList,List<Node> nodeList){
		Graph graph = new Graph();
		graph.setName("edge");
		graph.setConnections(edgeList);
		graph.setServerUpdateTime(System.currentTimeMillis());
		graph.setNodes(nodeList);
		String graphStr = gson.toJson(graph, Graph.class);
		log.info(graphStr);
		return graphStr;
	}
}
