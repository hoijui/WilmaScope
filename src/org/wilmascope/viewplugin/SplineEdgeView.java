package org.wilmascope.viewplugin;

import org.wilmascope.view.*;
import org.wilmascope.dotlayout.*;
import org.wilmascope.dotparser.EdgeClient;
import org.wilmascope.graph.Edge;
import java.awt.geom.Point2D;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Cone;
import java.util.*;
import java.util.StringTokenizer;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SplineEdgeView extends EdgeView implements org.wilmascope.dotlayout.Spline {
  public SplineEdgeView() {
    setTypeName("Spline");
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

  protected void init() {/**@todo: implement this org.wilmascope.view.GraphElementView abstract method*/}
  /**
   * curves is a vector of curves
   * each curve is a vector of control points (integer)
   */
  Vector curves, arrowPositions = new Vector();
  float width, height, scale;
  int x0, y0;
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
  Point3f[] calculate(Point2D.Float[] controlPoints) {
    float a, b, c, d, e, f, g, h;
    float x0, x1, x2, x3, y0, y1, y2, y3;
    float thisX, thisY;
    int steps = 10;
    int numCurves = controlPoints.length / 3;
    Point3f[] points = new Point3f[numCurves*steps+1];
    int i = 0;
    zLevel = getEdge().getStart().getPosition().z;
    points[i++] = new Point3f(controlPoints[0].x,controlPoints[0].y,zLevel);
    for (int n = 0; n<numCurves; n++) {
      x0 = controlPoints[n*3].x;   y0 = controlPoints[n*3].y;
      x1 = controlPoints[n*3+1].x; y1 = controlPoints[n*3+1].y;
      x2 = controlPoints[n*3+2].x; y2 = controlPoints[n*3+2].y;
      x3 = controlPoints[n*3+3].x; y3 = controlPoints[n*3+3].y;
      a = -x0 + 3*x1 - 3*x2 + x3;
      b = 3*x0 - 6*x1 + 3*x2;
      c = -3*x0 + 3*x1;
      d = x0;
      e = -y0 + 3*y1 - 3*y2 + y3;
      f = 3*y0 - 6*y1 + 3*y2;
      g = -3*y0 + 3*y1;
      h = y0;
      float step1 = 1.0f / (float)steps;

      for (float u=step1; u <= 1.001; u += step1)
      {
        thisX = ((a*u + b) * u + c) * u + d;
        thisY = ((e*u + f) * u + g) * u + h;
        points[i++] = new Point3f(thisX,thisY,zLevel);
      }
    }
    return points;
  }
  public void draw() {
    if(curves==null) return;
    BranchGroup b = new BranchGroup();
    Appearance a = getAppearance();
    Material material = new Material();
    material.setCapability(Material.ALLOW_COMPONENT_READ);
    Color3f c = new Color3f(getColour());
    material.setDiffuseColor(c);
    material.setAmbientColor(c);
    material.setSpecularColor(c);
    material.setEmissiveColor(c);
    material.setLightingEnable(true);
    a.setMaterial(material);
    int settings = GeometryArray.COORDINATES | GeometryArray.NORMALS;// | GeometryArray.COLOR_3;
    for(Iterator i = curves.iterator(); i.hasNext();) {
      Point3f[] myCoords = calculate((Point2D.Float[])i.next());
      LineStripArray myLines = new LineStripArray(myCoords.length,settings, new int[]{myCoords.length});
      myLines.setCoordinates(0, myCoords);
      Shape3D s = new Shape3D(myLines, a);
      makePickable(s);
      b.addChild(s);
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

      Cone cone=new Cone(scale*0.005f, 1.0f, Cone.GENERATE_NORMALS, a);
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
  public void setScale(float scale) {
    this.scale = scale;
  }

  /*
  public void setProperties(Properties p) {
    super.setProperties(p);
    String cs = p.getProperty("ControlPoints");
    if(cs == null) {
      System.err.println("Warning: Control points not available for Spline Edge View");
      return;
    }
    StringTokenizer st = new StringTokenizer(cs);
    curves = new Vector();
    while(st.hasMoreTokens()) {
      int n = Integer.parseInt(st.nextToken());
      Point2D.Float[] pnts = new Point2D.Float[n];
      for(int i = 0; i<n; i++) {
        pnts[i] = new Point2D.Float(
          Float.parseFloat(st.nextToken()),
          Float.parseFloat(st.nextToken()));
      }
      curves.add(pnts);
    }
    String as = p.getProperty("ArrowPositions");
    if(as == null) {
      System.err.println("Warning: Arrow positions not available for Spline Edge View");
      return;
    }
    st = new StringTokenizer(as);
    EdgeClient e = new EdgeClient(null,null) {
      public void setCurves(Vector c) {
      }
    };
    int arrows = Integer.parseInt(st.nextToken());
    for(int i=0;i<arrows;i++) {
      e.addArrow(
        Integer.parseInt(st.nextToken()),
        Integer.parseInt(st.nextToken())==1?true:false,
        new java.awt.Point(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()))
      );
    }
    arrowPositions = e.getArrowPositions();
    DotLayout l = (DotLayout)getEdge().getOwner().getLayoutEngine();

    this.x0 = l.bbXMin;
    this.y0 = l.bbYMin;
    this.scale = l.scale;
    width = l.width;
    height = l.height;
  }
  public Properties getProperties() {
    Properties p = super.getProperties();
    if(curves == null) {
      System.err.println("Warning: null control points in Spline Edge View");
      return p;
    }
    String s = new String();
    for(Iterator i = curves.iterator(); i.hasNext();) {
      Point2D.Float[] controlPoints = (Point2D.Float[])i.next();
      if(s.length()>0) {
        s = s.concat(" ");
      }
      s = s.concat(""+controlPoints.length);
      for(int j=0;j<controlPoints.length;j++) {
        Point2D.Float pnt = controlPoints[j];
        s = s.concat(" " + pnt.x + " " + pnt.y);
      }
    }
    p.setProperty("ControlPoints",s);
    if(arrowPositions.size()>0) {
      s = new String(""+arrowPositions.size());
      for(Iterator i = arrowPositions.iterator(); i.hasNext();) {
        EdgeClient.ArrowPosition ap = (EdgeClient.ArrowPosition)i.next();
        s = s.concat(" "+ap.curveIndex+" "+(ap.arrowAtStart?1:0)+" "+ap.position.x+" "+ap.position.y);
      }
      p.setProperty("ArrowPositions",s);
    }
    return p;
  }
  */
  float zLevel;
}
