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
import javax.media.j3d.Transform3D;
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
  public void calculateLayout() {
    balanced = false;

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
  }
  /** find closest point on the plane to the point argument
   * plane is determined by normal vector and centroid of root cluster
   */
  public void constrainPointToPlane(Point3f point, Vector3f displacement) {
      // plane is given by normal vector and centroid of root cluster
      Vector3f normal = root.getNormal();
      Point3f c = root.getPosition();

      // calculate vector from centroid to point
      displacement.sub(point, c);

      // find the projection of this vector on the normal
      float d = displacement.dot(normal);

      // thus the displacement is a scaling of the normal
      displacement.set(normal);
      displacement.scale(d);

      // move the point to the plane
      point.sub(displacement);
  }
  public float applyLayout() {
    NodeForceLayout nodeLayout;
    // reposition nodes, calculating maxNetForce as you go
    float maxNetForce=0;
    float currentNetForce=0;
    netTorque = new Vector3f();
    NodeList nodes = root.getNodes();
    EdgeList edges = root.getEdges();
    projectedBarycenter = nodes.getBarycenter();
    Vector3f displacement = new Vector3f();
    constrainPointToPlane(projectedBarycenter, displacement);
    //((NodeForceLayout)root.getLayout()).addForce(displacement);
    for(nodes.resetIterator(); nodes.hasNext();) {
      nodeLayout = (NodeForceLayout)(nodes.nextNode().getLayout());
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
    if(netTorque.length()>0) {
      rotate();
    }
    return maxNetForce;
  }
  private void rotate() {
    float angleDelta = netTorque.length()/(angularInertia);
    netTorque.normalize();
    if(angleDelta > 0.00000001) {
      root.rotate(new AxisAngle4f(netTorque, angleDelta));
    }
    Transform3D t = new Transform3D();
    t.setRotation(root.getOrientation());
    Vector3f normal = root.getNormal();
    normal.set(vY);
    t.transform(normal);
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
  public void setAngularInertia(float ai) {
    angularInertia = ai;
  }
  public float getAngularInertia() {
    return angularInertia;
  }
  public void addNetTorque(Vector3f torque) {
    netTorque.add(torque);
  }
  public Point3f getProjectedBarycenter() {
    return projectedBarycenter;
  }
  public void setConstrained() {
    constrained = true;
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
  private Vector3f netTorque = new Vector3f();
  private Vector3f vY = new Vector3f(0,1f,0);
  private Point3f projectedBarycenter;
  private float angularInertia = Constants.angularInertia;
  private boolean constrained = false;
}
