/*
 * The following source code is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Christine Wu however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform me(sainttale@hotmail.com)
 * if you do anything really cool with it.
 *
 *
 * -- Christine, 2003
 */
package org.wilmascope.areaChart3D;

/**
 * @author Christine
 *  
 */
import javax.media.j3d.Appearance;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * Class ValueLine create the marking lines for the chart
 */
public class ValueLine extends Shape3D {

  private Color3f color = new Color3f(1, 1, 1);

  private LineArray[] lines = new LineArray[4];

  /**
   * @param lineNum
   *          The number of the marking lines
   * @param length
   *          The length of the chart
   * @param width
   *          The width of the chart
   */
  public ValueLine(int lineNum, float length, float height, float width) {
    lineGeometry(lineNum, length, height, width);
    this.setGeometry(lines[0]);
    this.addGeometry(lines[1]);
    //this.addGeometry(lines[2]);
    this.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    this.setAppearance(lineAppearance());
  }

  private void lineGeometry(int lineNum, float length, float height, float width) {
    float deltaY = height / lineNum;
    float y = deltaY;
    Point3f[] coord1 = new Point3f[2 * lineNum];
    Color3f[] colors = new Color3f[2 * lineNum];
    for (int i = 0; i < 2 * lineNum; i = i + 2) {
      coord1[i] = new Point3f(0, y, 0);
      coord1[i + 1] = new Point3f(0, y, -length);
      y += deltaY;
      colors[i] = color;
      colors[i + 1] = color;
    }
    lines[0] = new LineArray(2 * lineNum, LineArray.COORDINATES
        | LineArray.COLOR_3);
    lines[0].setCoordinates(0, coord1);
    lines[0].setColors(0, colors);
    //////////////////////
    Point3f[] coord2 = new Point3f[2 * lineNum];
    y = deltaY;
    for (int i = 0; i < 2 * lineNum; i = i + 2) {
      coord2[i] = new Point3f(0, y, -length);
      coord2[i + 1] = new Point3f(width, y, -length);
      y += deltaY;
    }
    lines[1] = new LineArray(2 * lineNum, LineArray.COORDINATES
        | LineArray.COLOR_3);
    lines[1].setCoordinates(0, coord2);
    lines[1].setColors(0, colors);
    //////////////////////////
    Point3f[] coord3 = new Point3f[2 * lineNum];
    y = deltaY;
    for (int i = 0; i < 2 * lineNum; i = i + 2) {
      coord3[i] = new Point3f(width, y, -length);
      coord3[i + 1] = new Point3f(width, y, 0);
      y += deltaY;
    }
    lines[2] = new LineArray(2 * lineNum, LineArray.COORDINATES
        | LineArray.COLOR_3);
    lines[2].setCoordinates(0, coord3);
    lines[2].setColors(0, colors);
    ///////////////////////////////////////
    Point3f[] coord4 = new Point3f[2 * lineNum];
    y = deltaY;
    for (int i = 0; i < 2 * lineNum; i = i + 2) {
      coord4[i] = new Point3f(0, y, 0);
      coord4[i + 1] = new Point3f(width, y, 0);
      y += deltaY;
    }
    lines[3] = new LineArray(2 * lineNum, LineArray.COORDINATES
        | LineArray.COLOR_3);
    lines[3].setCoordinates(0, coord4);
    lines[3].setColors(0, colors);
  }

  /**
   * @return Returns the Array that contains the geometry of the marking lines
   */
  public LineArray[] getLineArrays() {
    return lines;
  }

  private Appearance lineAppearance() {
    Appearance materialAppear = new Appearance();
    Material material = new Material();
    LineAttributes lineAttrib = new LineAttributes();
    lineAttrib.setLineWidth(0.1f);
    materialAppear.setLineAttributes(lineAttrib);
    materialAppear.setMaterial(material);
    return materialAppear;

  }
}
