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
import org.wilmascope.graph.Node;
import org.wilmascope.graph.Cluster;
import java.util.Vector;
import javax.vecmath.*;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

public class ForceLayout implements LayoutEngine {

  public ForceLayout(Cluster root) {
    this.root = root;
    root.setLayoutEngine(this);
  }
  public float layout() {
    balanced = false;
    float maxVelocity = 0;
    for(int i = 0; i < iterations; i++) {
      if(balanced) {
        // If graph is already balanced then we don't need to calculate
        // new positions
        break;
      }

      // Calculate the force on each node for each of our forces
      for(int j = 0; j < forces.size(); j++) {
        Force f = (Force)forces.get(j);
        f.calculate();
      }
    }
    return maxVelocity;
  }
  public float applyForces(NodeList nodes, EdgeList edges) {
    NodeForceLayout nodeLayout;
    // reposition nodes, calculating maxNetForce as you go
    float maxNetForce=0;
    float currentNetForce=0;
    for(nodes.resetIterator(); nodes.hasNext();) {
      nodeLayout = (NodeForceLayout)(nodes.nextNode().getLayout());
      currentNetForce = nodeLayout.getNetForce().length();
      if(currentNetForce > maxNetForce) {
        maxNetForce = currentNetForce;
      }
      nodeLayout.applyForce(velocityAttenuation);
    }
    for(int i=0; i < edges.size(); i++) {
      edges.get(i).recalculate();
    }
    return maxNetForce;
  }
  /**
   * Set the number of iterations of the algorithm per call to
   * {@link #layout(org.wilmascope.graph.NodeList, org.wilmascope.graph.EdgeList)}
   */
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
  public int getIterations() {
    return iterations;
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
  public void setBalanced(boolean balanced) {
    this.balanced = balanced;
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
  }
  private BalancedEventClient balancedEventClient;
  // the number of iterations of the algorithm per call to layout
  private int iterations;
  // balanced is true when the graph has reached a stable state
  private boolean balanced;
  // an array of the forces to apply
  private Vector forces = new Vector();
  private float balancedThreshold = Constants.balancedThreshold;
  private float velocityAttenuation = Constants.velocityAttenuation;
  private Cluster root;
}
