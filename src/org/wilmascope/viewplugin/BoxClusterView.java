package org.wilmascope.viewplugin;
import org.wilmascope.view.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.ImageIcon;
import com.sun.j3d.utils.geometry.Box;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class BoxClusterView extends ClusterView {
  public BoxClusterView() {
    setTypeName("Box Cluster");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.pinkMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void draw() {
    org.wilmascope.graph.Cluster c = (org.wilmascope.graph.Cluster)getNode();
    double radius = (double)c.getRadius();
    setFullTransform(
      new Vector3d(radius,radius,radius),
      new Vector3f(c.getPosition()),
      c.getOrientation());
  }
  public void init() {
    setExpandedView();
    Box box = new Box(1f,1f,1f,getAppearance());
    getTransformGroup().addChild(box);
    makePickable(box.getShape(Box.FRONT));
    makePickable(box.getShape(Box.BACK));
    makePickable(box.getShape(Box.LEFT));
    makePickable(box.getShape(Box.RIGHT));
    makePickable(box.getShape(Box.TOP));
    makePickable(box.getShape(Box.BOTTOM));
  }
  protected float getCollapsedRadius(float density) {
    return (float)Math.pow(getCluster().getMass()/density,1.0d/3.0d);
  }
  public ImageIcon getIcon() {
    return new ImageIcon("images/boxcluster.png");
  }
}
