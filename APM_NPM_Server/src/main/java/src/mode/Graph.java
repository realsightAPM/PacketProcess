package src.mode;

import java.util.List;

public class Graph {
	private List<Node> nodes;
	private List<Edge> connections;
	private long serverUpdateTime;
	private String name;
	
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public List<Edge> getConnections() {
		return connections;
	}
	public void setConnections(List<Edge> connections) {
		this.connections = connections;
	}
	
	public long getServerUpdateTime() {
		return serverUpdateTime;
	}
	public void setServerUpdateTime(long serverUpdateTime) {
		this.serverUpdateTime = serverUpdateTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
