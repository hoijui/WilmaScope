package org.wilmascope.multiscalelayout;

/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3i;
import java.util.Vector;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MultiScaleNodeLayout extends org.wilmascope.graph.NodeLayout {
  Point3f position = new Point3f();
  float mass = 1;
  Vector3f delta = new Vector3f();
  Point3i cell = new Point3i(0,0,0);
  // level is not an integer because the nodes of abstractions created through
  // matching will have levels that are the mean of the child nodes
  float level = -1;
  int index;
  int tmpind;
  Object userData;
  public void setUserData(Object data) {
    this.userData = data;
  }
  public Object getUserData() {
    return userData;
  }

  boolean fixed;
  Vector neighbours = new Vector();

  String label;
  MultiScaleNodeLayout parent;
  public String toString() {
    return label;
  }
}
