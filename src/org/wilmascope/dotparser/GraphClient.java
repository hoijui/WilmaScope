package org.wilmascope.dotparser;
public interface GraphClient {
  public EdgeClient addEdge(NodeClient start, NodeClient end);
  public NodeClient addNode(String id);
  public void setBoundingBox(int x1, int y1, int x2, int y2);
}

