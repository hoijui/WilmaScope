package org.wilmascope.viewplugin;
import org.wilmascope.view.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Cone;
import javax.swing.ImageIcon;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class ConeClusterView extends ClusterView {
  public ConeClusterView() {
    setTypeName("Planar Cluster");
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

    //LODSphere sphere = new LODSphere(1f,getAppearance());
    //sphere.makePickable(this);
    //sphere.addToTransformGroup(getTransformGroup());

    Cone cone = new Cone(1f,0.2f,getAppearance());
    getTransformGroup().addChild(cone);
    makePickable(cone.getShape(Cone.BODY));
    makePickable(cone.getShape(Cone.CAP));
  }
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("/images/planarcluster.png"));
  }
}
