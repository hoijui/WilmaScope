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
        Point3f t = nodes.nextNode().getPosition();
        if(t.z>p.z) {
          p.set(t);
        }
      }
      Vector3f v = new Vector3f(p);
      //v.z+=columnCluster.getTopNode().getRadius();
      v.z+=0.11f;
      setTranslation(v);
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
  public void showLabel(String label) {
    Text2D text = new Text2D(label,new Color3f(1f,0,0),"dummy",30,java.awt.Font.PLAIN);
    makePickable(text);
    TransformGroup textTG = new TransformGroup();
    Transform3D trans = new Transform3D();
    trans.setTranslation(new Vector3f(-0.06f,-0.03f,0));
    trans.setScale(0.5f);
    textTG.setTransform(trans);
    textTG.addChild(text);
    setLabel(textTG);
  }
  public void setProperties(Properties p) {
    super.setProperties(p);
    String tp = p.getProperty("TopPosition");
    if(tp == null) {
      System.err.println("WARNING: ColumnView missing TopPosition");
      return;
    }
    StringTokenizer st = new StringTokenizer(tp);
    for(int i = 0;st.hasMoreTokens();i++) {
      position = new Point3f(
        Float.parseFloat(st.nextToken()),
        Float.parseFloat(st.nextToken()),
        Float.parseFloat(st.nextToken())
      );
    }
  }
  public Properties getProperties() {
    Properties p = super.getProperties();
    if(position == null) {
      System.err.println("WARNING: ColumnView missing TopPosition");
      return p;
    }
    p.setProperty("TopPosition",position.x+" "+position.y+" "+position.z);
    return p;
  }
}
