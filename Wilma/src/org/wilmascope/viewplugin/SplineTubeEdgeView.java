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
import org.wilmascope.dotlayout.*;
import org.wilmascope.dotparser.EdgeClient;
import org.wilmascope.graph.Edge;
import java.awt.geom.Point2D;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import java.util.*;
import java.util.StringTokenizer;
/**
 * An edge view which extrudes a tube out along a spline
 */

public class SplineTubeEdgeView extends EdgeView implements org.wilmascope.dotlayout.Spline {
  //
  // create the basic reference geometries from the shape sections of a cylinder
  //
  static Point3f[] tubePoints;
  static int[] tubeStripCounts;
  final static int xSize=10, ySize=10, segmentSize = 2*(xSize+1);
  final static float yStep = 1f/(float)ySize;
  static NormalGenerator normalGenerator = new NormalGenerator();
  {
    Cylinder c = new Cylinder(1f, 1f,0,xSize,ySize,null);
    GeometryStripArray tubeGeometry = getGeometry(c, Cylinder.BODY);
    tubePoints = new Point3f[tubeGeometry.getVertexCount()];
    tubeStripCounts = new int[tubeGeometry.getNumStrips()];
    loadGeometry(tubeGeometry, tubeStripCounts, tubePoints);
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
  }
  public SplineTubeEdgeView() {
    setTypeName("SplineTube");
  }

  protected void setupHighlightMaterial() {/**@todo: implement this org.wilmascope.view.GraphElementView abstract method*/}

  protected void setupDefaultMaterial() {
    Material material = new Material();
    material.setDiffuseColor(0.0f, 0.0f, 1.0f);
    material.setAmbientColor(0f,0f,0.4f);
    material.setShininess(50.0f);
    setupDefaultAppearance(material);
    getAppearance().setLineAttributes(new LineAttributes(3f,LineAttributes.PATTERN_SOLID,true));
  }

  /**
   * curves is a vector of curves
   * each curve is a vector of control points (integer)
   */
  Vector curves, arrowPositions = new Vector();
  float width, height, scale;
  int x0, y0;
  public void setScale(float scale) {
    this.scale = scale;
  }
  public void setCurves(float scale, int x0, int y0, int x1, int y1, Vector curves, Vector arrowPositions) {
    this.curves = new Vector();
    this.arrowPositions = arrowPositions;
    this.x0 = x0;
    this.y0 = y0;
    this.scale = scale;
    width = (float)(x1 - x0);
    height = (float)(y1 - y0);
    for(Iterator i = curves.iterator(); i.hasNext(); ) {
      Vector curve = (Vector)i.next();
      Point2D.Float[] pnts = new Point2D.Float[curve.size()];
      for(int j = 0; j<pnts.length; j++ ) {
        java.awt.Point p = (java.awt.Point)curve.get(j);
        pnts[j] = new Point2D.Float(
          scale * ((float)(p.x - x0)/width - 0.5f),
          scale * ((float)(p.y - y0)/width - 0.5f));
      }
      this.curves.add(pnts);
    }
  }
  /** This method and the next together implement The de-Casteljau Algorithm.
   *  I've modelled them on the example code at:
   * <a href="http://www.cs.huji.ac.il/~arik/java/ex2/">this site</a>
   * Thanks!
   * @return the linear interpolation of two points
   */
  Point2D.Float interpolate(Point2D.Float p0, Point2D.Float p1,float t) {
    return new Point2D.Float(t * p1.x + (1-t) * p0.x,
                             t * p1.y + (1-t) * p0.y);
  }
  /**
   * evaluates a bezier defined by the control polygon
   * which points are given in the array at the value t
   * returns the point on the curve (which is also returned in the first point in the
   * input array, the line from that point to the second point in the array gives
   * a tangent vector)
   */
  Point2D.Float evalBezier(Point2D.Float[] arr,float t) {
    for (int iter = arr.length ; iter > 0 ; iter--) {
      for (int i = 1 ; i < iter ; i++) {
        arr[i-1] = interpolate(arr[i-1],arr[i],t);
      }
    }
    return arr[0];
  }

  void splineTube(Point2D.Float[] controlPoints,BranchGroup b) {
    Point2D.Float[] t2pnts = new Point2D.Float[4];
    float radius = getRadius();
    int steps = 10;
    int numCurves = controlPoints.length / 3;
    int i = 0;
    zLevel = getEdge().getStart().getPosition().z;
    for (int n = 0; n<numCurves; n++) {
      Point3f[] taperedTubePoints = new Point3f[tubePoints.length];
      System.arraycopy(controlPoints,n*3,t2pnts,0,4);
      float y=0f;
      Vector2f v = new Vector2f(t2pnts[1].x-t2pnts[0].x,t2pnts[1].y-t2pnts[0].y);
      float cosTheta = -v.y/v.length();
      float sinTheta = (float)Math.sin(Math.acos(cosTheta));
      if(v.x>0) { sinTheta *= -1; }
      if(Float.isNaN(cosTheta)) {
        cosTheta=1.0f;
        sinTheta=0f;
      }
      for (int segment = 0; segment < ySize; segment++) {
        // odd numbered points are in lower layer of segment,
        // even numbered points are in higher layer
        // want to visit lower layer, then higher layer
        for (int j=1; true; j+=2) {
          if(j==segmentSize+1) {
            j=0;
            y+=yStep;
            System.arraycopy(controlPoints,n*3,t2pnts,0,4);
            if(y<0.9999f) {
              evalBezier(t2pnts,y);
              v.set(t2pnts[1].x-t2pnts[0].x,t2pnts[1].y-t2pnts[0].y);
            } else {
              v.set(t2pnts[3].x-t2pnts[2].x,t2pnts[3].y-t2pnts[2].y);
              evalBezier(t2pnts,y);
            }
            cosTheta = -v.y/v.length();
            sinTheta = (float)Math.sin(Math.acos(cosTheta));
            if(Float.isNaN(cosTheta)) {
              cosTheta=1.0f;
              sinTheta=0f;
            }
            if(v.x>0) { sinTheta *= -1; }
          } else if(j>=segmentSize) {
            break;
          }
          Point3f newPnt = new Point3f();
          Point3f oldPnt = tubePoints[segment*segmentSize+j];
          newPnt.x = -oldPnt.x*cosTheta*radius + t2pnts[0].x;
          newPnt.y = t2pnts[0].y+oldPnt.x * radius * sinTheta;
          newPnt.z = oldPnt.z*radius + zLevel;
          taperedTubePoints[segment*segmentSize+j]=newPnt;
        }
      }

      GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_STRIP_ARRAY);
      gi.setCoordinates(taperedTubePoints);
      gi.setStripCounts(tubeStripCounts);
      normalGenerator.generateNormals(gi);
      Shape3D tubeShape = new Shape3D(gi.getGeometryArray(), getAppearance());
      makePickable(tubeShape);
      b.addChild(tubeShape);
    }
  }
  public void draw() {
    if(curves==null) return;
    BranchGroup b = new BranchGroup();
    int settings = GeometryArray.COORDINATES | GeometryArray.NORMALS;// | GeometryArray.COLOR_3;
    for(Iterator i = curves.iterator(); i.hasNext();) {
      splineTube((Point2D.Float[])i.next(),b);

    }
    //Point3f[] arrowCoords = new Point3f[arrowPositions.size()*2];
    //int j=0;
    for(Iterator i = arrowPositions.iterator(); i.hasNext();) {
      EdgeClient.ArrowPosition arrowPos = (EdgeClient.ArrowPosition)i.next();
      Point3f end = new Point3f(
        scale * (((float)(arrowPos.position.x - x0))/width - 0.5f),
        scale * (((float)(arrowPos.position.y - y0))/width - 0.5f),
        zLevel);
      Point2D.Float p;
      Point2D.Float[] pnts = (Point2D.Float[])curves.get(arrowPos.curveIndex);
      if(arrowPos.arrowAtStart) {
        p = pnts[0];
      } else {
        p = pnts[pnts.length - 1];
      }
      Point3f start = new Point3f(p.x,p.y,zLevel);
      //arrowCoords[j++] = start;
      //arrowCoords[j++] = end;

      Cone cone=new Cone(2f*getRadius(), 1.0f, Cone.GENERATE_NORMALS, getAppearance());
      makePickable(cone.getShape(Cone.BODY));
      makePickable(cone.getShape(Cone.CAP));
      Transform3D transform = new Transform3D();
      Vector3f v = new Vector3f();
      v.sub(end,start);
      transform.setScale(new Vector3d(1,(double)v.length(),1));
      Vector3f vtrans = new Vector3f(v);
      vtrans.scale(0.5f);
      vtrans.add(start);
      transform.setTranslation(vtrans);
      Vector3f yaxis = new Vector3f(0,1f,0);
      Vector3f norm = new Vector3f();
      if(v.x == 0 && v.z == 0) {
        if(v.y > 0) {
          norm.z = 1;
        } else {
          norm.z = -1;
        }
      } else {
        norm.cross(yaxis,v);
      }
      transform.setRotation(new AxisAngle4f(norm, yaxis.angle(v)));
      TransformGroup coneTransform = new TransformGroup(transform);
      coneTransform.addChild(cone);
      b.addChild(coneTransform);
    }
    /*
    if(j!=0){
      LineArray arrows = new LineArray(arrowCoords.length,settings);
      arrows.setCoordinates(0, arrowCoords);
      Shape3D s = new Shape3D(arrows, a);
      makePickable(s);
      b.addChild(s);
    }
    */
    addLiveBranch(b);
  }
  public void init() {
  }
  /**
   * draw the edge correctly between the start and end nodes
   */
  public void draw2() {
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
  float zLevel;
  GeometryArray tubeGeometryArray;
  Point3f[] taperedTubePoints;
}