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
/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
/**
 * a Cluster is a set of Nodes ({@link Node}) and Edges
 * ({@link Edge}) sharing the same ({@link LayoutEngine}).
 * This class is the main interface for the graph package for
 * adding {@link GraphElement}s
 */
public class Cluster extends Node {
  // the list of nodes in the cluster
  private NodeList nodes = new NodeList();
  // the list of edges internal to the cluster
  private EdgeList internalEdges = new EdgeList();

  public NodeList getNodes() {
    return nodes;
  }
  /**
   * Add a {@link Node} to the Cluster
   * @param node the Node to add
   */
  public void addNode(Node node) {
    nodes.add(node);
    node.setOwner(this);
    EdgeList neighbourEdges;
    if(node instanceof Cluster && ((Cluster)node).isExpanded()) {
      neighbourEdges = ((Cluster)node).getExternalEdges();
    } else {
      neighbourEdges = node.getEdges();
    }
    for(int i=0; i<neighbourEdges.size(); i++ ){
      Edge edge = neighbourEdges.get(i);
      addInternalEdge(edge);
    }
    calcMass();
  }
  public EdgeList getExternalEdges() {
    NodeList allNodes = getAllNodes();
    EdgeList allEdges = allNodes.getEdges();
    EdgeList externalEdges = new EdgeList();
    for(int i=0; i<allEdges.size(); i++) {
      Edge edge = allEdges.get(i);
      if(!(
        allNodes.contains(edge.getStart()) &&
        allNodes.contains(edge.getEnd())
      )) {
        externalEdges.add(edge);
      }
    }
    return externalEdges;
  }
  public EdgeList getInternalEdges() {
    return internalEdges;
  }

  public boolean isAncestor(Node n) {
    if(nodes.contains(n)) {
      return true;
    }
    ClusterList clusters = nodes.getClusters();
    for(int i=0;i<clusters.size();i++) {
      if(clusters.get(i).isAncestor(n)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Remove a {@link Node} from the Cluster
   * @param node which Node to remove
   */
  public void removeNode(Node n) {
    nodes.remove(n);
  }

  /**
   * Add an {@link Edge} to the Cluster
   */
  public void addInternalEdge(Edge e) {
    Cluster cluster;
    if((cluster = e.getStart().getCommonAncestor(e.getEnd()))!=null) {
      cluster.addInternalEdgeHere(e);
    }
  }
  private void addInternalEdgeHere(Edge e) {
    internalEdges.add(e);
    e.setOwner(this);
  }

  public void remove(GraphElement e) {
    if(e instanceof Node) {
      removeNode((Node)e);
    } else if(e instanceof Edge) {
      removeEdge((Edge)e);
    }
  }
  /**
   * Remove an {@link Edge} from the Cluster
   */
  public void removeEdge(Edge e) {
    internalEdges.remove(e);
  }

  /**
   * The default constructor - an empty cluster
   */
  public Cluster() {
  }
  /**
   * Create a new cluster with the specified {@link NodeView}
   */
  public Cluster(NodeView view) {
    super(view);
  }
  private boolean expanded = true;
  public boolean isExpanded() {
    return expanded;
  }
  public void draw() {
    if(expanded) {
      internalEdges.draw();
      nodes.draw();
      calcRadius();
    }
    super.draw();
  }

  /**
   * Set the {@link LayoutEngine} for the cluster
   */
  public void setLayoutEngine(LayoutEngine l) {
    layoutEngine = l;
  }

  /**
   * Get the {@link LayoutEngine} used by the cluster
   */
  public LayoutEngine getLayoutEngine() {
    return layoutEngine;
  }

  public void hideChildren() {
    nodes.hide();
    internalEdges.hide();
  }
  public void showChildren(org.wilmascope.view.GraphCanvas gc) {
    if(expanded) {
      nodes.show(gc);
      internalEdges.show(gc);
    }
  }
  public NodeList getAllNodes() {
    NodeList allNodes = new NodeList(this.nodes);
    ClusterList clusters = this.nodes.getClusters();
    for(int i=0;i<clusters.size();i++) {
      allNodes.addAll(clusters.get(i).getAllNodes());
    }
    return allNodes;
  }

  public void collapse() {
    this.expanded = false;
    hideChildren();
    EdgeList externalEdges = new EdgeList();
    NodeList leafNodes = getLeafNodes();
    for(int i=0;i<leafNodes.size();i++) {
      Node node = leafNodes.get(i);
      EdgeList nodeEdges = new EdgeList(node.getEdges());
      for(int j=0;j<nodeEdges.size();j++) {
        Edge edge = nodeEdges.get(j);
        if(!leafNodes.contains(edge.getNeighbour(node))) {
          externalEdges.add(edge);
          edge.collapse(this, node);
        }
      }
    }
    super.setEdges(externalEdges);
  }
  private NodeList getLeafNodes() {
    NodeList leafNodes = new NodeList();
    for(int i=0; i<nodes.size(); i++) {
      Node node = nodes.get(i);
      if(node instanceof Cluster) {
        Cluster cluster = (Cluster)node;
        if(cluster.isExpanded()) {
          leafNodes.addAll(((Cluster)node).getLeafNodes());
        } else {
          leafNodes.add(cluster);
        }
      } else {
        leafNodes.add(node);
      }
    }
    return leafNodes;
  }
  public void expand(org.wilmascope.view.GraphCanvas graphCanvas) {
    expanded = true;
    nodes.setPosition(getPosition());
    nodes.show(graphCanvas);
    internalEdges.show(graphCanvas);
    EdgeList externalEdges = super.getEdges();
    for(int i=0; i<externalEdges.size(); i++) {
      externalEdges.get(i).expand(this);
    }
    super.setEdges(new EdgeList());
  }

  /**
   * Lay out the nodes according to the current graph and layout engine
   */
  public float layout() {
    if(expanded) {
      float maxVelocity = nodes.getClusters().layout();
      float velocity = layoutEngine.layout(nodes, internalEdges);
      if(velocity > maxVelocity) {
        maxVelocity = velocity;
      }
      return maxVelocity;

      // Let's postpone calculating the radius until just before we draw since
      // at the moment there's nothing in Layout that needs it...
      // calcRadius();
    }
    return 0;
  }
  // The radius at any given time should be the distance from the centre of the
  // cluster to the farthest node + that node's radius and a bit
  private void calcRadius() {
    float newRadius = 0;
    float tmpRadius;
    for (int i = 0; i < nodes.size(); i++) {
      Node member = nodes.get(i);
      tmpRadius = getPosition().distance(member.getPosition()) + member.getRadius();
      if(tmpRadius > newRadius) {
        newRadius = tmpRadius;
      }
    }
    newRadius += 0.01;
    setRadius(newRadius);
  }


  // A layout engine for use by the cluster
  private LayoutEngine layoutEngine;

  /**
   * Moves this cluster (and its' internal nodes) by the specified amount
   * @param delta the change vector
   */
  public void reposition(Vector3f delta) {
    super.reposition(delta);
    if(expanded) {
      nodes.reposition(delta);
    }
  }
  public void delete() {
    super.delete();
    NodeList tmplist = new NodeList(nodes);
    for(int i=0; i<tmplist.size(); i++) {
      Node n = tmplist.get(i);
      n.delete();
    }
  }
  /**
   * Moves this cluster to the new position, and repositions all of its
   * member nodes appropriately.
   * @param newPosition the position to which everything will be moved
   */
  public void setPosition(Point3f newPosition) {
    Vector3f delta = new Vector3f();
    delta.sub(getPosition(), newPosition);
    reposition(delta);
  }

  public void freeze(boolean balanced) {
    layoutEngine.setBalanced(balanced);
    nodes.setBalanced(balanced);
  }

  public void setBalanced(boolean balanced) {
    layoutEngine.setBalanced(balanced);
  }

  /**
   * Add the specified nodes to this cluster
   */
  public void addNodes(NodeList startNodes) {
    for(int i=0;i<startNodes.size(); i++) {
      addNode(startNodes.get(i));
    }
    Vector3f delta = new Vector3f(nodes.getBarycenter());
    delta.sub(getPosition());
    super.reposition(delta);
  }
  public float calcMass() {
    float mass = nodes.calcMass();
    setMass(mass);
    return mass;
  }
}




