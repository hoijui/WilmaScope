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

import org.wilmascope.graph.*;
import javax.vecmath.*;
import java.util.Vector;

/** A force field that will force all directed edges to align themselves
 * with its direction vector
 */
public class DirectedField extends Force {
  Vector3f vector;

  Vector3f t =new Vector3f();

  Vector3f r =new Vector3f();

  public DirectedField(float strengthConstant,Vector3f vector) {
    super(strengthConstant, "DirectedField");
    this.vector = vector;
    vector.normalize();
  }
  public DirectedField(float strengthConstant) {
    this(strengthConstant,new javax.vecmath.Vector3f(0,-1f,0));
  }

  /** calculate the deltas to apply to the node positions due to
   * the effects of this force
   * @param edges the list of edges to which this force is to
   * be applied
   */
  public void calculate(NodeList nodes, EdgeList edges) {
    for(int i=0; i < edges.size(); i++) {
      Edge edge = (Edge)edges.get(i);
      Vector3f edgeVector = edge.getVector();
      if(edge.isDirected()) {
        t.cross(vector, edgeVector);
        r.cross(edgeVector, t);
        //r.scale(edge.getMagnetism());
        r.scale(strengthConstant);
        ((NodeForceLayout)edge.getEnd().getLayout()).addForce(r);
        ((NodeForceLayout)edge.getStart().getLayout()).subForce(r);
      }
    }
  }
}
