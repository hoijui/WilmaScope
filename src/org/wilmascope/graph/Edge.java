/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope organisation
 * (www.wilmascope.org) however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform us (tgdwyer@yahoo.com)
 * if you do anything really cool with it.
 *
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma
 *
 * -- Tim Dwyer, 2001
 */
package org.wilmascope.graph;

import javax.vecmath.Vector3f;
import java.util.Hashtable;
/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

/**
* An Edge links two nodes
*/
public class Edge extends GraphElement {
  public Edge() {
  }
  /**
  * Creates an edge between two nodes
  * @param start Node
  * @param end Node
  */
  public Edge(Node start, Node end) {
    setStart(start);
    setEnd(end);
  }
  /**
  * Creates an edge between two nodes, and sets the view
  * @param start Node
  * @param end Node
  * @param view EdgeView
  */
  public Edge(Node start, Node end, EdgeView view) {
    this(start,end);
    setView(view);
  }
  /**
  * Associates a ({@link EdgeView}) view object with this edge.
  * (For any edge, there can be only one view object)
  * @param view {@link EdgeView}
  */
  public void setView(EdgeView view) {
    this.view = view;
    view.setEdge(this);
    recalculateMultiEdgeOffsets();
  }
  /**
   * When multiple edges exist between a pair of nodes we want them offset
   * from each other by a small amount so that they are both visible.
   */
  public void recalculateMultiEdgeOffsets() {
    EdgeList commonEdges = start.getCommonEdges(end);
    for(int i=0;i<commonEdges.size();i++) {
      Edge e = commonEdges.get(i);
      int direction = 1;
      if(e.getStart()!=start) {
        direction = -1;
      }
      e.getView().setMultiEdgeOffset(i,commonEdges.size(),direction);
    }
  }
  /**
  * @return the {@link EdgeView} for the edge object.
  * (For any edge object, there can be only one view object)
  */
  public EdgeView getView() {
    return (EdgeView)view;
  }
  /**
  * Associates an EdgeLayout object with <strong>this</strong> edge
  */
  public void setLayout(EdgeLayout layout) {
    this.layout = layout;
    layout.setEdge(this);
  }
  /**
  * @return {@link EdgeLayout} object for this Edge.
  */
  public EdgeLayout getLayout() {
    return (EdgeLayout)layout;
  }

  private Node start;
  private Node end;
  /**
  * @return the start node for this edge
  */
  public Node getStart() {
    return start;
  }
  /**
  * Sets a new start Node for this edge.
  * @param newStart - a replacement Node
  */
  public void setStart(Node newStart) {
    start = newStart;
    newStart.addEdge(this);
  }
  /**
  * Sets a new end Node for this edge.
  * @param newEnd - a replacement Node
  */
  public void setEnd(Node newEnd) {
    end = newEnd;
    newEnd.addEdge(this);
  }
  /**
  * @return the End node of <strong>this</strong> edge.
  */
  public Node getEnd() {
    return end;
  }
  /**
   * swap the start and end nodes so that the edge effectively reverses
   * direction
   */
  public void reverseDirection() {
    // have to remove the edge reference from the nodes first as setStart and
    // setEnd call addEdge
    start.removeEdge(this);
    end.removeEdge(this);
    Node n = start;
    setStart(end);
    setEnd(n);
    // have to recalculate the multi Edge offsets taking into account
    // the new direction of this edge
    recalculateMultiEdgeOffsets();
  }
  /**
  * @return the length of <strong>this</strong> edge.
  */
  public float getLength() {
    return length;
  }
  public Node getNeighbour(Node node) {
    if(node==start) {
      return end;
    } else if(node==end) {
      return start;
    } else {
      return null;
    }
  }

  /**
   * assuming oldNode is either the start or end of this edge, this method
   * replaces oldNode with newNode.
   */
  public void swapNode(Node oldNode, Node newNode) {
    if(oldNode==start) {
      start.removeEdge(this);
      setStart(newNode);
    } else if(oldNode==end) {
      end.removeEdge(this);
      setEnd(newNode);
    }
  }
  /**
   * used to temporarily change the edge to point to cluster instead of
   * node (assuming node is one of the edge's nodes).
   * {@link #expand(Cluster)} undoes the effect.
   */
  public void collapse(Cluster cluster, Node node) {
    collapseHistoryTable.put(cluster,node);
    swapNode(node, cluster);
  }

  /**
   * undoes the effect of {@link #collapse} for a given cluster
   */
  public void expand(Cluster cluster) {
    Node node = (Node)collapseHistoryTable.remove(cluster);
    swapNode(cluster, node);
  }

  /**
  * Delete all references to this edge... make it go away... forever!
  */
  public void delete() {
    owner.remove(this);
    start.removeEdge(this);
    end.removeEdge(this);
    view.delete();
    view = null;
    layout.delete();
    layout = null;
    recalculateMultiEdgeOffsets();
  }
  public void recalculate() {
    vector.sub(end.getPosition(), start.getPosition());
    length = vector.length();
  }
  public void setVector(Vector3f v) {
    vector.set(v);
  }
  public Vector3f getVector() {
    return vector;
  }
  public boolean isDirected() {
    return directed;
  }
  private float length;
  // Vector along the length of the edge
  private Vector3f vector = new Vector3f();
  private Hashtable collapseHistoryTable = new Hashtable();
  private boolean directed = true;
}