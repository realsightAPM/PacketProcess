package src.mode;

import java.util.List;

public class Node {
	private String displayName;
	private String renderer;
	private String name;
	private List<Node> nodes;
	private Metadata metadata;
	private String showClass;
	private List<Edge> edge;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getRenderer() {
		return renderer;
	}
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	public String getShowClass() {
		return showClass;
	}
	public void setShowClass(String showClass) {
		this.showClass = showClass;
	}
	public List<Edge> getEdge() {
		return edge;
	}
	public void setEdge(List<Edge> edge) {
		this.edge = edge;
	}
	
	
	
}
