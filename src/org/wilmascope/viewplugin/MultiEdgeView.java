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

import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.view.Colours;
import org.wilmascope.view.CompoundEdgeView;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.ViewConstants;

import com.sun.j3d.utils.geometry.Cylinder;

/**
 * Graphical representation of the edge
 *
 * @author Tim Dwyer
 * @version 1.0
 */

public class MultiEdgeView extends EdgeView implements CompoundEdgeView {
  float length = 1.0f;
  public MultiEdgeView() {
    setTypeName("Multi Edge");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.blueMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void init() {
  }
  /* (non-Javadoc)
   * @see org.wilmascope.view.CompoundEdgeView#addBend(javax.vecmath.Point3f)
   */
  public void addBend(Point3f bendPosition) {
    bends.add(bendPosition);
  }
  public void draw() {
    if (bg != null) {
      bg.detach();
    }
    bg = new BranchGroup();
    bg.setCapability(BranchGroup.ALLOW_DETACH);
    Point3f start = getEdge().getStart().getPosition();
    for(Point3f bend:bends) {
      addCylinder(start,bend);
      start=bend;
    }
    addCylinder(start,getEdge().getEnd().getPosition());
    addLiveBranch(bg);
  }
  void addCylinder(Point3f start, Point3f end) {
    Cylinder cylinder = new Cylinder(1.0f, 0.9f, getAppearance());
    Transform3D t3d = new Transform3D();
    Vector3f v = new Vector3f(end);
    v.sub(start);
    t3d.setScale(new Vector3d((double)getRadius(),(double)v.length(),(double)getRadius()));
    Vector3f pos = new Vector3f(v);
    pos.scaleAdd(0.5f,start);
    t3d.setTranslation(pos);
    t3d.setRotation(getAxisAngle4f(ViewConstants.vY,v));
    TransformGroup tg = new TransformGroup(t3d);
    tg.addChild(cylinder);
    makePickable(cylinder.getShape(Cylinder.BODY));
    bg.addChild(tg);
  }
  ArrayList<Point3f> bends = new ArrayList<Point3f>();
  BranchGroup bg = null;
}
