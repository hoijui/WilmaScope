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

package org.wilmascope.viewplugin;

import org.wilmascope.view.*;
import org.wilmascope.gui.QueryFrame;
import org.wilmascope.graph.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Text2D;
import java.util.*;

public class ColumnClusterView extends ClusterView {
  public void draw() {
    try {
      Point3f p = new Point3f();
      NodeList nodes = ((Cluster)getNode()).getAllNodes();
      for(nodes.resetIterator();nodes.hasNext();) {
        org.wilmascope.graph.Node n= nodes.nextNode();
        Point3f t = n.getPosition();
        if(t.z>p.z) {
          p.set(t);
          getNode().setRadius(((TubeNodeView)n.getView()).getTopRadius());
        }
      }
      Vector3f v = new Vector3f(p);
      //v.z+=columnCluster.getTopNode().getRadius();
      v.z+=0.11f;
      double r = getNode().getRadius() * 0.5f;
      // center(ish) label
      v.x-=0.13f*r;
      v.y-=0.05f*r;
      setResizeTranslateTransform(new Vector3d(r,r,r),v);
    } catch(NullPointerException e) {System.out.println("WARNING: Null pointer in ColumnClusterView.draw()");}
  }
  Point3f position;
  public void setPositionRef(Point3f position) {
    this.position = position;
  }
  public ColumnClusterView() {
    setTypeName("Column Cluster");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.pinkMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void init() {
    setExpandedView();
  }
  public void setLabelColour(Color3f c) {
    this.labelColour = c;
  }
  Color3f labelColour = new Color3f(0,0,0);
  public void showLabel(String label) {
    Text2D text = new Text2D(label,labelColour,"dummy",30,java.awt.Font.PLAIN);
    makePickable(text);
    TransformGroup textTG = new TransformGroup();
    Transform3D trans = new Transform3D();
    textTG.setTransform(trans);
    textTG.addChild(text);
    setLabel(textTG);
  }
}
