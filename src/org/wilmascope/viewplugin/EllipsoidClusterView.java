package org.wilmascope.viewplugin;

import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Plane;
import org.wilmascope.view.ClusterView;
import org.wilmascope.view.Colours;
import org.wilmascope.view.LODSphere;

import com.sun.j3d.utils.geometry.Cone;

/**
 * An ellipsoid shaped cluster that is (roughly) fitted to its constituent nodes
 * 
 * @author Tim Dwyer
 * @version 1.0
 */

public class EllipsoidClusterView extends ClusterView {
  public EllipsoidClusterView() {
    setTypeName("Ellipsoid Cluster");
  }

  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.blueMaterial);
  }

  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }

  public void draw() {
    org.wilmascope.graph.Cluster c = (org.wilmascope.graph.Cluster) getNode();
    double radius = (double) getRadius();
    Plane p = c.getNodes().getBestFitPlane();
    Vector3f norm = p.getNormal();
    AxisAngle4f angle = getAxisAngle4f(new Vector3f(0, 1, 0), norm);
    double[] s = p.getSingularValues();
    setFullTransform(new Vector3d(s[1], s[2], s[0]), new Vector3f(p
        .getCentroid()), angle);
  }

  public void init() {
    setExpandedView();
    LODSphere sphere = new LODSphere(1.0f, getAppearance(), new float[] { 6f,
        10f, 30f });
    sphere.makePickable(this);
    sphere.addToTransformGroup(getTransformGroup());
  }

  public ImageIcon getIcon() {
    return new ImageIcon(org.wilmascope.images.Images.class
        .getResource("planarcluster.png"));
  }
}
