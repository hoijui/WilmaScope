package org.wilmascope.gmlparser;
public interface GraphClient {
	public void addNode(String id, String label);
  public void addEdge(
    String startID,
    String endID,
    String label,
    String arrowPosition);
  public void addEdge(
    String startID,
    String endID,
    String arrowPosition);
}
