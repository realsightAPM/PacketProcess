package src.graphutil;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import src.mode.Edge;
import src.mode.Graph;
import src.mode.Node;

@Service
public class JsonTranslate {
	
	private Gson gson = new Gson();
	
	public String getGraphJson(List<Edge> edgeList,List<Node> nodeList){
		Graph graph = new Graph();
		graph.setName("edge");
		graph.setConnections(edgeList);
		graph.setServerUpdateTime(System.currentTimeMillis());
		graph.setNodes(nodeList);
		return gson.toJson(graph, Graph.class);
	}
}
