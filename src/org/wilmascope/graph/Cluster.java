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
import javax.vecmath.Quat4f;
import javax.vecmath.AxisAngle4f;
import java.util.Hashtable;
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
  // an angle by which the Cluster and all its contents will be rotated at the
  // next draw
  private Quat4f orientation = new Quat4f();

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
    // get this node's edges.  Edges between the node to be added and nodes
    // that are members of this cluster will be added to this cluster's
    // internal edge list -- addEdge is a bit brute force: it
    // adds the edge to the common ancestor of the two ends -- see the notes
    // on additional witchcraft in addEdge
    EdgeList newNodeEdges = node.getEdges();
    for(newNodeEdges.resetIterator(); newNodeEdges.hasNext();){
      Edge edge = newNodeEdges.nextEdge();
      Node neighbour = edge.getNeighbour(node);
      // add the edge at the appropriate level in the cluster hierarchy
      addEdge(edge);
    }
    calcMass();
  }
  public EdgeList getInternalEdges() {
    return internalEdges;
  }

  public boolean isAncestor(Node n) {
    if(nodes.contains(n)) {
      return true;
    }
    ClusterList clusters = nodes.getClusters();
    for(clusters.resetIterator();clusters.hasNext();) {
      if(clusters.nextCluster().isAncestor(n)) {
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
    // remove all the node's edges
    EdgeList edges = n.getEdges();
    for(edges.resetIterator(); edges.hasNext();) {
      Edge e = edges.nextEdge();
      // if edge is already an external edge then remove it altogether
      if(super.getEdges().contains(e)) {
        super.removeEdge(e);
      } else if(e != null) {
        // otherwise it was an internal edge that should now be made external
        internalEdges.remove(e);
        Node neighbour = e.getNeighbour(n);
        if(neighbour==null) {
          System.out.println("Warning... null neighbour while making internal"
            +" edge external during node delete");
          // this may occur when cluster is collapsed
        } else {
          addExternalEdge(e, e.getNeighbour(n));
        }
      } // if e was external but the cluster was collapsed so that e has been
        // promoted then our reference to the edge will be null... nothing
        // needs to occur as the edge will be deleted from the owner
    }
  }

  /**
   * Add an {@link Edge} to the Cluster
   * Note that the edge may not finish up being added to this cluster,
   * it will be added to the lowest common ancestor of the two ends of the
   * edge.
   * @param e the edge to add
   */
  public void addEdge(Edge e) {
    Cluster cluster;
    if((cluster = e.getStart().getCommonAncestor(e.getEnd()))!=null) {
      cluster.addInternalEdgeHere(e);
    } else {
      throw new Error("No root cluster!  Edge not added!");
    }
  }
  /**
   * Add a reference to an external edge, an edge that has one end in the
   * cluster and the other outside
   * @param edge the external edge to add
   * @param portalNode the connection point of the edge inside the cluster
   */

  private void addExternalEdge(Edge edge, Node portalNode) {
    // the EdgeList we inherited from Node is our external node list
    super.addEdge(edge);
    // make a mapping from the external edge to its connection point
    // inside the cluster (a "portal" node)
    portalNodes.put(edge, portalNode);
  }
  /**
   * An external edge is one which has one end in this cluster and one end in
   * another cluster that is not a child (or descendent) of this cluster.
   * A portal node is a node in this cluster which has an external edge
   * This method looks up the portal node in this cluster for a specific
   * external edge.
   * @param edge an edge external to this cluster
   */
  public Node getPortalNode(Edge edge) {
    return (Node)portalNodes.get(edge);
  }
  /**
   * Add an internal edge to this cluster...
   * If the edge is already a member of this cluster it will be removed
   * and then added again.  Nothing like brute force to get the job done.
   */
  protected void addInternalEdgeHere(Edge e) {
    if(!internalEdges.contains(e)) {
      e.setOwner(this);
      internalEdges.add(e);
      // just in case the edge was already external to this cluster
      //   ie one end of the edge was already in this cluster and the other
      // has just been added
      super.removeEdge(e);
    }
    // for the each node of the edge, if the parent of that node is not
    // this cluster (ie the cluster that now owns the edge) then this edge
    // is external to that parent cluster
    Node n = e.getStart();
    Cluster owner = n.getOwner();
    if(owner != this) {
      owner.addExternalEdge(e,n);
    }
    n = e.getEnd();
    owner = n.getOwner();
    if(owner != this) {
      owner.addExternalEdge(e,n);
    }
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
    portalNodes.remove(e);
    removeExternalEdge(e);
  }

  public void removeExternalEdge(Edge e) {
    super.removeEdge(e);
  }

  /**
   * The default constructor - an empty cluster
   */
  public Cluster() {
    id=-1;
  }
  /**
   * Create a new cluster with the specified {@link NodeView}
   */
  public Cluster(NodeView view) {
    super(view);
    id = idcounter++;
    normal = new Vector3f(0f,1f,0f);
    orientation.set(new AxisAngle4f(normal,0f));
  }
  private static int idcounter=0;
  private final int id;
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
  public Vector3f getNormal() {
    return normal;
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
    for(clusters.resetIterator();clusters.hasNext();) {
      allNodes.addAll(clusters.nextCluster().getAllNodes());
    }
    return allNodes;
  }

  public void collapse() {
    userCollapsed = true;
    subCollapse();
  }

  private void subCollapse() {
    this.expanded = false;
    ClusterList childClusters = nodes.getClusters();
    for(childClusters.resetIterator(); childClusters.hasNext();) {
      childClusters.nextCluster().subCollapse();
    }

    hideChildren();
    EdgeList externalEdges = super.getEdges();
    for(externalEdges.resetIterator(); externalEdges.hasNext();) {
      Edge e = externalEdges.nextEdge();
      e.collapse(this, (Node)portalNodes.get(e));
    }
  }
  private NodeList getLeafNodes() {
    NodeList leafNodes = new NodeList();
    for(nodes.resetIterator(); nodes.hasNext();) {
      Node node = nodes.nextNode();
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
    userCollapsed = false;
    subExpand(graphCanvas);
  }
  private void subExpand(org.wilmascope.view.GraphCanvas graphCanvas) {
    if(userCollapsed) {
      return;
    }
    expanded = true;
    nodes.setPosition(getPosition());
    nodes.show(graphCanvas);
    internalEdges.show(graphCanvas);
    EdgeList externalEdges = super.getEdges();
    for(externalEdges.resetIterator(); externalEdges.hasNext();) {
      externalEdges.nextEdge().expand(this);
    }
    ClusterList cs = nodes.getClusters();
    for(cs.resetIterator(); cs.hasNext();) {
      cs.nextCluster().subExpand(graphCanvas);
    }
  }

  /**
   * Lay out the nodes according to the current graph and layout engine
   */
  public void calculateLayout() {
    if(expanded) {
      nodes.getClusters().calculateLayout();
      layoutEngine.calculateLayout();

      // Let's postpone calculating the radius until just before we draw since
      // at the moment there's nothing in Layout that needs it...
      // calcRadius();
    }
  }
  public float applyLayout() {
    if(expanded) {
      float maxVelocity = nodes.getClusters().applyLayout();
      float velocity = layoutEngine.applyLayout();
      if(velocity > maxVelocity) {
        maxVelocity = velocity;
      }
      return maxVelocity;
    }
    return 0;
  }

  // The radius at any given time should be the distance from the centre of the
  // cluster to the farthest node + that node's radius and a bit
  private void calcRadius() {
    float newRadius = 0;
    float tmpRadius;
    for (nodes.resetIterator(); nodes.hasNext();) {
      Node member = nodes.nextNode();
      tmpRadius = getPosition().distance(member.getPosition()) + member.getRadius();
      if(tmpRadius > newRadius) {
        newRadius = tmpRadius;
      }
    }
    // add a little bit extra to the radius to make sure it covers all nodes
    newRadius += 0.1;
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
  /**
   * delete a cluster and its contents
   */
  public void delete() {
    NodeList tmplist = new NodeList(nodes);
    for(tmplist.resetIterator(); tmplist.hasNext();) {
      tmplist.nextNode().delete();
    }
    super.delete();
  }
  /**
   * Moves this cluster to the new position, and repositions all of its
   * member nodes appropriately.
   * @param newPosition the position to which everything will be moved
  public void setPosition(Point3f newPosition) {
    Vector3f delta = new Vector3f();
    delta.sub(getPosition(), newPosition);
    reposition(delta);
  }
   */

  public AxisAngle4f getRotationAngle() {
    return rotationAngle;
  }

  public void setOrientation(Quat4f orientation) {
    this.orientation = orientation;
  }
  public Quat4f getOrientation() {
    return orientation;
  }

  public void rotate(AxisAngle4f angle) {
    Quat4f orientationDelta = new Quat4f();
    orientationDelta.set(angle);
    orientation.mul(orientationDelta);
    this.rotationAngle = angle;
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
    for(startNodes.resetIterator();startNodes.hasNext();) {
      addNode(startNodes.nextNode());
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
  // a table mapping this cluster's external edges to their
  // connection points (portal nodes) in the cluster
  private Hashtable portalNodes = new Hashtable();
  private Vector3f normal = new Vector3f();
  private AxisAngle4f rotationAngle = new AxisAngle4f();
  private boolean userCollapsed = false;
}




