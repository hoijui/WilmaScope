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

import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.swing.ImageIcon;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Edge;
import org.wilmascope.view.Colours;
import org.wilmascope.view.Constants;
import org.wilmascope.view.EdgeView;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Graphical representation of the edge
 *
 * @author Tim Dwyer
 * @version 1.0
 */

public class TaperedEdgeView extends EdgeView {
  //
  // create the basic reference geometries from the shape sections of a cylinder
  //
  static float topRadius = 1f, bottomRadius = 1f;
  static Point3f[] tubePoints;
  static int[] tubeStripCounts;
  static Point3f[] topPoints;
  static int[] topStripCounts;
  static Point3f[] bottomPoints;
  static int[] bottomStripCounts;
  static NormalGenerator normalGenerator = new NormalGenerator();
  {
    Cylinder c = new Cylinder(1f, 1f);
    GeometryStripArray tubeGeometry = getGeometry(c, Cylinder.BODY);
    GeometryStripArray topGeometry = getGeometry(c, Cylinder.TOP);
    GeometryStripArray bottomGeometry = getGeometry(c, Cylinder.BOTTOM);
    tubePoints = new Point3f[tubeGeometry.getVertexCount()];
    topPoints = new Point3f[topGeometry.getVertexCount()];
    bottomPoints = new Point3f[bottomGeometry.getVertexCount()];
    tubeStripCounts = new int[tubeGeometry.getNumStrips()];
    topStripCounts = new int[topGeometry.getNumStrips()];
    bottomStripCounts = new int[bottomGeometry.getNumStrips()];
    loadGeometry(tubeGeometry, tubeStripCounts, tubePoints);
    loadGeometry(topGeometry, topStripCounts, topPoints);
    loadGeometry(bottomGeometry, bottomStripCounts, bottomPoints);
  }
  private static GeometryStripArray getGeometry(Cylinder c, int section) {
    return (GeometryStripArray) c.getShape(section).getGeometry();
  }
  private static void loadGeometry(
    GeometryStripArray geometry,
    int[] stripCounts,
    Point3f[] points) {
    for (int i = 0; i < points.length; i++) {
      points[i] = new Point3f();
    }
    geometry.getCoordinates(0, points);
    geometry.getStripVertexCounts(stripCounts);
    /*
    // rotate so that cylinder stands lengthwise in the z-dimension
    Transform3D rotateTransform = new Transform3D();
    rotateTransform.rotX(Math.PI/2);
    for(int i=0;i<points.length; i++) {
        rotateTransform.transform(points[i]);
    }
    */
  }

  float length = 1.0f;
  public TaperedEdgeView() {
    setTypeName("Tapered Edge");
  }
  protected void setupDefaultMaterial() {
    Material material = new Material();
    material.setDiffuseColor(0.0f, 0.0f, 1.0f);
    material.setAmbientColor(0f, 0f, 0.4f);
    material.setShininess(50.0f);
    setupDefaultAppearance(material);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  } /**
   * adjust the radius of the top of the tube
   * @parameter r xScale factor, ie the resulting top radius will be r * node radius
   */
  static public void setTopRadius(float r) {
    topRadius = r;
  }
  /**
   * adjust the radius of the bottom of the tube
   * @parameter r xScale factor, ie the resulting bottom radius will be r * node radius
   */
  static public void setBottomRadius(float r) {
    bottomRadius = r;
  }
  public void init() {
    Point3f[] taperedTubePoints = new Point3f[tubePoints.length];
    setRadius(0.1f);
    for (int i = 0; i < tubePoints.length; i++) {
      if (i % 2 == 0) {
        taperedTubePoints[i] =
          new Point3f(
            tubePoints[i].x * topRadius,
            tubePoints[i].y,
            tubePoints[i].z * topRadius);
      } else {
        taperedTubePoints[i] =
          new Point3f(
            tubePoints[i].x * bottomRadius,
            tubePoints[i].y,
            tubePoints[i].z * bottomRadius);
      }
    }
    GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_STRIP_ARRAY);
    gi.setCoordinates(taperedTubePoints);
    gi.setStripCounts(tubeStripCounts);
    normalGenerator.generateNormals(gi);
    Shape3D tubeShape = new Shape3D(gi.getGeometryArray(), getAppearance());
    makePickable(tubeShape);
    addTransformGroupChild(tubeShape);
  }
  /**
   * draw the edge correctly between the start and end nodes
   */
  public void draw() {
    Edge e = getEdge();
    e.recalculate();
    double l = e.getLength();
    // avoids non-affine transformations, by making sure edge always has
    // non-zero length
    if (e.getLength() == 0) {
      e.setVector(Constants.gc.getVector3f("MinVector"));
      l = e.getVector().length();
    }
    Vector3f v = new Vector3f(e.getVector());
    v.scaleAdd(0.5f, e.getStart().getPosition());
    setFullTransform(new Vector3d(getRadius(), l, getRadius()), v, getPositionAngle());
  }
  public ImageIcon getIcon() {
    return new ImageIcon("images/taperedEdge.png");
  }
}
