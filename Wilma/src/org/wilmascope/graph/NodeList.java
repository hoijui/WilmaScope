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

import java.util.*;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
public class NodeList extends List<Node>{
  private ClusterList clusters = new ClusterList();
  public NodeList() {}
  public NodeList(NodeList nodes) {
    elements = new java.util.Vector<Node>(nodes.elements);
  }
  public void add(Node node) {
    elements.add(node);
    if(node instanceof Cluster) {
      clusters.add((Cluster)node);
    }
  }
  public ClusterList getClusters() {
    return clusters;
  }
  public void remove(Node node) {
    elements.remove(node);
    if(node instanceof Cluster) {
      clusters.remove((Cluster)node);
    }
  }
  /**
   * removes all references to the nodes in l in this list and if they are
   * clusters then also from the cluster list beneath this NodeList
   */
  public void removeAll(NodeList l) {
    for(Iterator<Node> i=l.iterator();i.hasNext();) {
      Node n = i.next();
      if(n instanceof Cluster) {
        clusters.remove((Cluster)n);
      }
      i.remove();
    }
  }
  public Node get(int index) {
    return (Node)elements.get(index);
  }
  public boolean contains(Node node) {
    return elements.contains(node);
  }
  public void reposition(Vector3f delta) {
    for(Node n:elements) {
      n.reposition(delta);
    }
  }
  public void setPosition(Point3f position) {
    for(Node n:elements) {
      n.setPosition(position);
    }
  }
  public void hide() {
    super.hide();
    for(Cluster c:clusters) {
      c.hideChildren();
    }
  }
  public void show(org.wilmascope.view.GraphCanvas gc) {
    super.show(gc);
    for(Cluster c:clusters) {
      c.showChildren(gc);
    }
  }
  public EdgeList getEdges() {
    Set<Edge> edges = new HashSet<Edge>();
    for(Node n:elements) {
      edges.addAll(n.getEdges().getElementsVector());
    }
    return new EdgeList(edges);
  }
  /**
   * @return the barycenter of all the nodes in the NodeList
   */
  public Point3f getBarycenter() {
    Point3f barycenter = new Point3f();
    for(Node n:elements) {
      barycenter.add(n.getPosition());
    }
    barycenter.scale(1f/(float)elements.size());
    return barycenter;
  }
  public float getWidth() {
    float xMin=Float.MAX_VALUE, xMax=Float.MIN_VALUE;
    for(Node n:elements) {
      Point3f p = n.getPosition();
      if(p.x<xMin) { xMin = p.x; }
      if(p.x>xMax) { xMax = p.x; }
    }
    return xMax - xMin;
  }
  public float getHeight() {
    float yMin=Float.MAX_VALUE, yMax=Float.MIN_VALUE;
    for(Node n:elements) {
      Point3f p = n.getPosition();
      if(p.y<yMin) { yMin = p.y; }
      if(p.y>yMax) { yMax = p.y; }
    }
    return yMax - yMin;
  }
  /**
   * Finds the extreme corners of the bounding box around these nodes
   * @param bottomLeft bottom left corner of the bounding box
   * @param topRight top right corner of the bounding box
   * @param centroid midway between bottomLeft and topRight
   */
  public void getBoundingBoxCorners(Point3f bottomLeft, Point3f topRight, Point3f centroid) {
    bottomLeft.x=Float.MAX_VALUE;
    topRight.x=Float.MIN_VALUE;
    bottomLeft.y=Float.MAX_VALUE;
    topRight.y=Float.MIN_VALUE;
    bottomLeft.z=Float.MAX_VALUE;
    topRight.z=Float.MIN_VALUE;
    for(Node n:elements) {
      Point3f p = n.getPosition();
      if(p.x<bottomLeft.x) { bottomLeft.x = p.x; }
      if(p.x>topRight.x) { topRight.x = p.x; }
      if(p.y<bottomLeft.y) { bottomLeft.y = p.y; }
      if(p.y>topRight.y) { topRight.y = p.y; }
      if(p.z<bottomLeft.z) { bottomLeft.z = p.z; }
      if(p.z>topRight.z) { topRight.z = p.z; }
    }
    centroid.add(bottomLeft,topRight);
    centroid.scale(1f/2f);
  }

}
