package org.wilmascope.dotparser;
public abstract class NodeClient {
  public NodeClient(String id) {
    this.id = id;
  }
  public abstract void setPosition(int x, int y);
  public String getID() {
    return id;
  }
  public String id;
}
