package org.wilmascope.dotparser;
import java.util.Vector;
import java.awt.Point;
public abstract class EdgeClient {
  public class ArrowPosition {
    public ArrowPosition(int curveIndex, boolean arrowAtStart, Point position) {
      this.curveIndex = curveIndex;
      this.arrowAtStart = arrowAtStart;
      this.position = position;
    }
    public int curveIndex;
    public boolean arrowAtStart;
    public Point position;
  }
  public EdgeClient(NodeClient start, NodeClient end) {
    this.start = start;
    this.end = end;
  }
  public abstract void setCurves(java.util.Vector curves);
  public void addArrow(int curveIndex, boolean arrowAtStart, Point position) {
    arrowPositions.add(new ArrowPosition(curveIndex, arrowAtStart, position));
  }
  public Vector getArrowPositions() {
    return arrowPositions;
  }
  public void setLayer(String layer) {
    this.layer = layer;
  }
  public NodeClient start, end;
  public String layer;
  public Vector arrowPositions = new Vector();
}
