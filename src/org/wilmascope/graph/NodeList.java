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
public class NodeList extends List{
  private ClusterList clusters = new ClusterList();
  public NodeList() {}
  public NodeList(NodeList nodes) {
    elements = new java.util.Vector(nodes.elements);
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
    for(l.resetIterator();l.hasNext();) {
      remove(l.nextNode());
    }
  }
  public Node get(int index) {
    return (Node)elements.get(index);
  }
  public boolean contains(Node node) {
    return elements.contains(node);
  }
  public void reposition(Vector3f delta) {
    for(resetIterator(); hasNext();) {
      nextNode().reposition(delta);
    }
  }
  public void setPosition(Point3f position) {
    for(resetIterator(); hasNext();) {
      nextNode().setPosition(position);
    }
  }
  public void hide() {
    super.hide();
    for(clusters.resetIterator(); clusters.hasNext();) {
      clusters.nextCluster().hideChildren();
    }
  }
  public void show(org.wilmascope.view.GraphCanvas gc) {
    super.show(gc);
    for(clusters.resetIterator(); clusters.hasNext();) {
      clusters.nextCluster().showChildren(gc);
    }
  }
  public EdgeList getEdges() {
    Set edges = new HashSet();
    for(resetIterator();hasNext();) {
      edges.addAll(nextNode().getEdges().getElementsVector());
    }
    return new EdgeList(edges);
  }
  /**
   * @return the barycenter of all the nodes in the NodeList
   */
  public Point3f getBarycenter() {
    Point3f barycenter = new Point3f();
    for(resetIterator(); hasNext();) {
      barycenter.add(nextNode().getPosition());
    }
    barycenter.scale(1f/(float)elements.size());
    return barycenter;
  }
  public float getWidth() {
    float xMin=Float.MAX_VALUE, xMax=Float.MIN_VALUE;
    for(resetIterator();hasNext();) {
      Point3f p = nextNode().getPosition();
      if(p.x<xMin) { xMin = p.x; }
      if(p.x>xMax) { xMax = p.x; }
    }
    return xMax - xMin;
  }
  public float getHeight() {
    float yMin=Float.MAX_VALUE, yMax=Float.MIN_VALUE;
    for(resetIterator();hasNext();) {
      Point3f p = nextNode().getPosition();
      if(p.y<yMin) { yMin = p.y; }
      if(p.y>yMax) { yMax = p.y; }
    }
    return yMax - yMin;
  }

  public final Node nextNode() {
    return (Node)next();
  }
}
