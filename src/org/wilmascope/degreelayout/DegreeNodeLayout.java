/*
 * Created on May 17, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.degreelayout;

import java.util.Properties;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;

/**
 * @author cmurray
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DegreeNodeLayout extends NodeLayout {
  private static final float maxMove = 0.3f;

  public static final float delta = 0.5f;

  private Vector3f force = new Vector3f(0.0f, 0.0f, 0.0f);

  public void setForce(float xf, float yf, float zf) {
    force.x = xf;
    force.y = yf;
    force.z = zf;
  }

  public void addForce(Vector3f f) {
    force.x += f.x;
    force.y += f.y;
    force.z += f.z;
  }

  public void scaleForce() {
    if (force.length() > maxMove) {
      force.normalize();
      force.scale(maxMove);
    }
    //force.scale(0.1f);
  }

  public Vector3f getForce() {
    return force;
  }

  public void applyForce() {
    Node node = getNode();
    if (isFixedPosition()) {
      return;
    }
    // Move the node
    node.reposition(force);
    if (levelConstraint >= 0) {
      Point3f p = node.getPosition();
      p.z = layerScale * (float) levelConstraint;
    }
    force.set(0.0f, 0.0f, 0.0f);
  }

  public void setLevelConstraint(int level) {
    levelConstraint = level;
  }

  int levelConstraint = -1;

  float layerScale = 1f;


}