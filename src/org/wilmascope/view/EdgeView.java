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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Edge;

/*
 * Any EdgeView plugin must extend this class
 * 
 * @author Tim Dwyer
 * @version 1.0
 */
public abstract class EdgeView extends GraphElementView
implements org.wilmascope.graph.EdgeView, View2D {
  /**
   * draw the edge correctly between the start and end nodes
   */
  public void draw() {
    edge.recalculate();
    double l = edge.getLength();
    // avoids non-affine transformations, by making sure edge always has
    // non-zero length
    if(l==0) {
      edge.setVector(ViewConstants.gc.getVector3f("MinVector"));
      l=edge.getVector().length();
    }
    // length of the edge should not include the radii of the nodes
    l-=edge.getStart().getView().getRadius()+edge.getEnd().getView().getRadius();
    setFullTransform(
      new Vector3d(radius,l,radius),
      getPositionVector(),
      getPositionAngle());
  }
  // Returns the angle between the initVector and the target vector
  // used by redraw.
  public AxisAngle4f getPositionAngle() {
    return getAxisAngle4f(initVector,edge.getVector());
  }
  // return the current position, mid-point, of the edge
  public Vector3f getPositionVector() {
    Vector3f v = new Vector3f(edge.getVector());
    v.scaleAdd(0.5f,edge.getStart().getPosition());
    if(edge.getEnd().getView().getRadius()!=edge.getStart().getView(  ).getRadius()) {
      Vector3f offset = new Vector3f(edge.getVector());
      offset.normalize();
      offset.scale((edge.getEnd().getView().getRadius()-edge.getStart().getView().getRadius())/2f);
      v.sub(offset);
    }
    // Calculate the offset from other edges
    //   as a vector normal to the z axis and the edge
    if(multiEdgeOffset!=0f) {
      Vector3f vMultiEdgeOffset = new Vector3f();
      if(direction > 0) {
        vMultiEdgeOffset.cross(edge.getVector(), ViewConstants.vZ);
      } else {
        vMultiEdgeOffset.cross(ViewConstants.vZ, edge.getVector());
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
    return new ImageIcon(org.wilmascope.images.Images.class.getResource("edge.png"));
  }
  protected void showLabel(String text) {
    //addLabel(text, 10.0, new Point3f(0.0f,4.0f,1.0f), ViewConstants.vZero, getAppearance());
	/**
	 * Modified by Xiaoyan
	 * to fix the bug of edge labels
	 * 
	 * add a new method in GraphElementView.java
	 * 	protected void addLabel(String text, Vector3d scale, Point3f originPosition, Vector3f vTranslation, Appearance apText)
	 * to set the scale in x,y,z axis seperately.
	 * 
	 * 20 is half size of the font(40); Node Label size=40;
	 */  
	//System.out.println("radius = "+radius);
    addLabel(text, 
    		new Vector3d(20,radius*text.length(),20), 
    		new Point3f(0.0f,0.0f,0.0f),
    		ViewConstants.vZero,
            getAppearance());
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
  public void draw2D(Renderer2D r, Graphics2D g, float transparency) {
    float thickness = r.scaleX(getRadius());
    g.setStroke(new BasicStroke(thickness));

    Color3f c = new Color3f();
    getAppearance().getMaterial().getDiffuseColor(c);
    g.setColor(c.get());

    Point3f start = getEdge().getStart().getPosition();
    Point3f end = getEdge().getEnd().getPosition();
    Vector3f v = new Vector3f();
    v.sub(end,start);
    v.scale(0.9f);
    Point3f lineEnd = new Point3f(start);
    lineEnd.add(v);
    r.linePath(g,start,lineEnd);
  }
  
  private Vector<WeakReference<NodeGeometryObserver>> geometryObservers;

//  private void notifyGeometryObservers() {
//	    if (geometryObservers != null) {
//	      for (Iterator<WeakReference<NodeGeometryObserver>> i = geometryObservers
//	          .iterator(); i.hasNext();) {
//	        WeakReference<NodeGeometryObserver> wr = i.next();
//	        NodeGeometryObserver o = wr.get();
//	        if (o != null) {
//	          o.nodeGeometryChanged(this);
//	        } else {
//	          i.remove();
//	        }
//	      }
//	    }
//	  }

  
  private Edge edge;
  // A vector giving the default orientation of the edgeCylinder
  private static Vector3f initVector = ViewConstants.vY;
  // offset to allow viewing of multiple edges between the same nodes
  private float multiEdgeOffset=0f;
  // default edge radius
  private float radius = 0.02f;
}
