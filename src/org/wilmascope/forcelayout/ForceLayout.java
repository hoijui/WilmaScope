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
package org.wilmascope.forcelayout;

import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.NodeList;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.ClusterList;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.Cluster;
import java.util.Vector;
import javax.vecmath.*;
import javax.media.j3d.Transform3D;
/**
 * Main class force for calculating forces on all nodes
 * and moving them incrementally.
 */

public class ForceLayout implements LayoutEngine {

  public ForceLayout(Cluster root) {
    this.root = root;
    root.setLayoutEngine(this);
  }
  public void calculateLayout() {
    // Calculate the force on each node for each of our forces
    for(int j = 0; j < forces.size(); j++) {
      Force f = (Force)forces.get(j);
      f.calculate();
    }
  }
  float cool = 1f;
  public void reset() {
    cool = 1f;
    ClusterList l = root.getNodes().getClusters();
    for(l.resetIterator(); l.hasNext();) {
        l.nextCluster().getLayoutEngine().reset();
    }
  }
  public boolean applyLayout() {
    NodeForceLayout nodeLayout;
    // reposition nodes, calculating maxNetForce as you go
    float maxNetForce=0;
    float currentNetForce=0;
    NodeList nodes = root.getNodes();
    EdgeList edges = root.getInternalEdges();
    for(nodes.resetIterator(); nodes.hasNext();) {
      nodeLayout = (NodeForceLayout)(nodes.nextNode().getLayout());
      nodeLayout.getNetForce().scale(cool);
      //cool*=0.99;
      currentNetForce = nodeLayout.getNetForce().length();
      if(currentNetForce > maxNetForce) {
        maxNetForce = currentNetForce;
      }
      nodeLayout.applyForce(velocityAttenuation);
      if(constrained) {
        nodeLayout.applyConstraint();
      }
    }
    for(int i=0; i < edges.size(); i++) {
      edges.get(i).recalculate();
    }
    if(maxNetForce<balancedThreshold) {
      if(balancedEventClient!=null) balancedEventClient.callback();
      reset();
      return true;
    }
    return false;
  }
  /**
   * Add a force to ForceLayout's list of forces to apply
   */
  public void addForce(Force force) {
    forces.add(force);
    force.setCluster(root);
  }
  /**
   * Remove a force from ForceLayout's list of forces to apply
   */
  public void removeForce(Force force) {
    forces.remove(force);
  }
  public void removeAllForces() {
    forces.clear();
  }
  public Vector getForces() {
    return forces;
  }
  /**
   * Get a reference to one of our forces by name
   */
  public Force getForce(String name) {
    for(int i = 0; i<forces.size(); i++) {
      Force force = (Force)forces.get(i);
      if(name.equals(force.getTypeName()))
        return force;
    }
    return null;
  }
  public void setBalancedEventClient(BalancedEventClient c) {
    balancedEventClient = c;
  }
  public void setBalancedThreshold(float threshold) {
    this.balancedThreshold = threshold;
  }
  public float getBalancedThreshold() {
    return balancedThreshold;
  }
  public float getVelocityAttenuation() {
    return velocityAttenuation;
  }
  public void setVelocityAttenuation(float va) {
    velocityAttenuation = va;
    System.out.println("VA="+va);
  }
  public void setConstrained() {
    constrained = true;
  }
  public org.wilmascope.graph.NodeLayout createNodeLayout() {
    return new NodeForceLayout();
  }
  public org.wilmascope.graph.EdgeLayout createEdgeLayout() {
    return new EdgeForceLayout();
  }
  // an array of the forces to apply
  Vector forces = new Vector();
  float velocityAttenuation = Constants.velocityAttenuation;
  Cluster root;
  Vector3f vY = new Vector3f(0,1f,0);
  boolean constrained = false;
  private float balancedThreshold = 0.01f;
  private BalancedEventClient balancedEventClient = null;
}