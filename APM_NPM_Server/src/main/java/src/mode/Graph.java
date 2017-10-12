package src.mode;

import java.util.LinkedList;
import java.util.List;

public class Graph {
	private List<Node> nodes = new LinkedList<Node>() ;
	private List<Edge> connections = new LinkedList<Edge>();
	private long serverUpdateTime = System.currentTimeMillis();
	
	
	private String name= "us-west-2";
   private String renderer="global";
  //private  int maxVolume=500;
	
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
	
	public String getRenderer() {
		return renderer;
	}
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	/*public int getMaxVolume() {
		return maxVolume;
	}
	public void setMaxVolume(int maxVolume) {
		this.maxVolume = maxVolume;
	}
	*/
	
}
