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

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.ImageIcon;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.columnlayout.ColumnLayout;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.NodeList;
import org.wilmascope.view.ClusterView;
import org.wilmascope.view.Colours;

import com.sun.j3d.utils.geometry.Text2D;

public class ColumnClusterView extends ClusterView {
  public void draw() {
    try {
      Point3f p = new Point3f();
      Cluster c = (Cluster)getNode();
      NodeList nodes = c.getAllNodes();
      float nodeHeight = ((ColumnLayout)c.getLayoutEngine()).getStrataSeparation();
      for (nodes.resetIterator(); nodes.hasNext();) {
        org.wilmascope.graph.Node n = nodes.nextNode();
        Point3f t = n.getPosition();;
        TubeView tnv = (TubeView)n.getView();
        if (t.z > p.z) {
          p.set(t);
          c.setRadius(tnv.getTopRadius());
        }
        tnv.setHeight(nodeHeight);
      }
      Vector3f v = new Vector3f(p);
      // want the label floating just above (0.01f) the top node
      v.z += nodeHeight/2f+0.01f;
      double r = getNode().getRadius() * 0.5f;
      // center(ish) label
      v.x -= 0.15f * r;
      v.y -= 0.04f * r;
      setResizeTranslateTransform(new Vector3d(r, r, r), v);
    } catch (NullPointerException e) {
      System.out.println("WARNING: Null pointer in ColumnClusterView.draw()");
    }
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
  Color3f labelColour = new Color3f(0, 0, 0);
  String[] labels;
  public void setLabel(String[] labels) {
    this.labels = labels;
    TransformGroup textTG = new TransformGroup();
    Transform3D trans = new Transform3D();
    trans.setTranslation(new Vector3f(0,0.05f*(float)(labels.length-1),0));
    textTG.setTransform(trans);
    for (int i = 0; i < labels.length; i++) {
      Text2D text =
        new Text2D(labels[i], labelColour, "dummy", 20, java.awt.Font.PLAIN);
      makePickable(text);
      TransformGroup textTG2 = new TransformGroup();
      trans = new Transform3D();
      trans.setTranslation(new Vector3f(0f, -0.1f * (float)i, 0f));
      textTG2.setTransform(trans);
      textTG2.addChild(text);
      textTG.addChild(textTG2);
    }
    setLabel(textTG);
  }
  public void setLabel(String label) {
    setLabel(new String[]{label});
  }
  public String getLabel() {
    return labels[0];
  }
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("/images/column.png"));
  }
}