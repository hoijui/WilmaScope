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

package org.wilmascope.view;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Edge;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */
public abstract class EdgeView extends GraphElementView
implements org.wilmascope.graph.EdgeView {
  /**
   * draw the edge correctly between the start and end nodes
   */
  public void draw() {
    edge.recalculate();
    double l = edge.getLength();
    // avoids non-affine transformations, by making sure edge always has
    // non-zero length
    if(l==0) {
      edge.setVector(Constants.gc.getVector3f("MinVector"));
      l=edge.getVector().length();
    }
    // length of the edge should not include the radii of the nodes
    l-=edge.getStart().getRadius()+edge.getEnd().getRadius();
    setFullTransform(
      new Vector3d(radius,l,radius),
      getPositionVector(),
      getPositionAngle());
  }
  // Returns the angle between the initVector and the target vector
  // used by redraw.
  public AxisAngle4f getPositionAngle() {
    Vector3f norm = new Vector3f();
    Vector3f v = edge.getVector();
    norm.cross(initVector,v);
    return new AxisAngle4f(norm.x,norm.y,norm.z,initVector.angle(v));
  }
  // return the current position, mid-point, of the edge
  public Vector3f getPositionVector() {
    Vector3f v = new Vector3f(edge.getVector());
    v.scaleAdd(0.5f,edge.getStart().getPosition());
    if(edge.getEnd().getRadius()!=edge.getStart().getRadius()) {
      Vector3f offset = new Vector3f(edge.getVector());
      offset.normalize();
      offset.scale((edge.getEnd().getRadius()-edge.getStart().getRadius())/2f);
      v.sub(offset);
    }
    // Calculate the offset from other edges
    //   as a vector normal to the z axis and the edge
    if(multiEdgeOffset!=0f) {
      Vector3f vMultiEdgeOffset = new Vector3f();
      if(direction > 0) {
        vMultiEdgeOffset.cross(edge.getVector(), Constants.vZ);
      } else {
        vMultiEdgeOffset.cross(Constants.vZ, edge.getVector());
      }
      vMultiEdgeOffset.normalize();
      vMultiEdgeOffset.scale(multiEdgeOffset);
      v.add(vMultiEdgeOffset);
    }
    return v;
  }
  public void setMultiEdgeOffset(int edgeIndex, int edgeCount, int direction) {
    multiEdgeOffset = ((float)edgeIndex - ((float)edgeCount-1f)/2f) * 0.05f;
    this.direction = direction;
  }
  private int direction=1;
  public Edge getEdge() {
    return edge;
  }
  public void setEdge(Edge edge) {
    this.edge = edge;
  }
  public void setHueByWeight(float minHue, float maxHue) {
    // seeing as we are using hues the wrong way round (ie, blue has higher hue
    // value than yellow but we consider yellow to indicate a greater weight
    // value than blue... usually: maxHue < minHue
    float hue = maxHue + (minHue - maxHue) * (1 - edge.getWeight());
    setColour(java.awt.Color.getHSBColor(hue,1f,1f));
  }

  public ImageIcon getIcon() {
    return new ImageIcon("images/edge.png");
  }
  protected void showLabel(String text) {
    addLabel(text, 1.0d, new Point3f(0.0f,4.0f,1.0f), Constants.vZero, getAppearance());
  }
  public void setRadius(float radius) {
    this.radius = radius;
  }
  public float getRadius() {
    return radius;
  }
  public void setProperties(Properties p) {
    super.setProperties(p);
    String radius = p.getProperty("Radius");
    if(radius!=null) {
      setRadius(Float.parseFloat(radius));
    }
  }
  public Properties getProperties() {
    Properties p = super.getProperties();
    p.setProperty("Radius",""+this.radius);
    return p;
  }

  private Edge edge;
  // A vector giving the default orientation of the edgeCylinder
  private static Vector3f initVector = Constants.vY;
  // offset to allow viewing of multiple edges between the same nodes
  private float multiEdgeOffset=0f;
  // default edge radius
  private float radius = 0.02f;
}
