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

import org.wilmascope.graph.NodeLayout;
import org.wilmascope.graph.Node;

import javax.vecmath.Vector3f;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

/**
 * This class encapsulates the physical properties of a node
 * in relation to the {@link ForceLayout} such as mass, and the
 * forces acting upon it.
 */

public class NodeForceLayout implements NodeLayout {
  public void delete() {
    node = null;
  }
  /** Set the node for the NodeForceLayout */
  public void setNode(Node node) {
    this.node = node;
  }

  /** Return the node for the NodeForceLayout */
  public Node getNode() {
    return node;
  }

  /** Add a force vector which will act on this NodeForceLayout */
  public void addForce(Vector3f force) {
    netForce.add(force);
  }

  /**
   * 'Subtract' a force vector
   */
  public void subForce(Vector3f force) {
    netForce.sub(force);
  }

  /** Get the aggregate (or net) force acting on this NodeForceLayout */
  public Vector3f getNetForce() {
    return netForce;
  }

  /** Adjust the node's position by calculating an acceleration due to the
   *  forces on the node, then applying that acceleration to the velocity of
   *  the node and then scale that velocity by the
   * attenuation factor.  Also applies a 'friction' force.
   * This causes the graph to settle at a stable position.
   * The friction force is directly proportional to the velocity and the
   * radius of the node, thus large nodes (especially expanded clusters) will
   * have greater "air resistance?" and will thus not vibrate as easily as
   * small nodes.
   * @param attenuation scale factor for the velocity
   */
  public void applyForce(float attenuation) {
    if(node.isFixedPosition()) {
      return;
    }
    // Calculate friction based on current velocity
    Vector3f friction = new Vector3f(velocity);
    friction.scale(Constants.frictionCoefficient * node.getRadius());
    netForce.sub(friction);
    // Acceleration (change in velocity) = F/m
    float sf = attenuation / node.getMass();
    acceleration.scale(sf,netForce);
    netForce.set(Constants.vZero);
    // Clip acceleration to its maximum... prevents bizarre behaviour
    clip(acceleration,Constants.maxAcceleration);
    velocity.add(acceleration);

    // Clip velocity at terminal velocity
    clip(velocity,Constants.terminalVelocity);

    // Move the node
    node.reposition(velocity);
  }
  /* If a {@link vec} is longer than {@link maxLength} then normalise it.
   * @param vec input vector
   * @param maxLength length beyond which {@link vec} is clipped
   * @return True if normalisation occurs
   */
  private static boolean clip(Vector3f vec,float maxLength) {
    if (vec.length() > maxLength) {
      vec.scale( maxLength / vec.length());
      return true;
    }
    return false;
  }
  private Node node;
  //The sum of all force vectors on the node
  private Vector3f netForce = new Vector3f();
  // The velocity with which the node was travelling after the last time the
  // forces were applied.
  private Vector3f velocity = new Vector3f();
  // Acceleration of the node due to the forces on the node
  private Vector3f acceleration = new Vector3f();
}
