package org.wilmascope.viewplugin;

import org.wilmascope.view.*;

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
 * -- Tim Dwyer, 2002
 */
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.ImageIcon;
import org.wilmascope.view.Colours;
public class LineEdgeView extends EdgeView {
  public LineEdgeView() {
    setTypeName("LineEdge");
  }
  protected void setupHighlightMaterial() {
    /**@todo: implement this org.wilmascope.view.GraphElementView abstract method*/
  }
  protected void setupDefaultMaterial() {
    /**@todo: implement this org.wilmascope.view.GraphElementView abstract method*/
  }
  LineArray myLine;
  protected void init() {
    int settings = GeometryArray.ALLOW_COORDINATE_WRITE
                 | GeometryArray.ALLOW_COLOR_WRITE
                 | GeometryArray.COORDINATES
                 | GeometryArray.NORMALS
                 | GeometryArray.COLOR_3;
    Point3f[] myCoords = new Point3f[]{new Point3f(0,-0.5f,0), new Point3f(0,0.5f,0)};
    myLine = new LineArray(2,settings);
    myLine.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    myLine.setCapability(LineArray.ALLOW_COLOR_WRITE);
    myLine.setCoordinates(0, myCoords);
    myLine.setColor(0,((GraphElementView)getEdge().getStart().getView()).getColor3f());
    myLine.setColor(1,((GraphElementView)getEdge().getEnd().getView()).getColor3f());
    Shape3D s = new Shape3D(myLine);
    makePickable(s);
    addTransformGroupChild(s);
  }
  public void draw() {
    myLine.setCoordinate(0,getEdge().getStart().getPosition());
    myLine.setCoordinate(1,getEdge().getEnd().getPosition());
  }
  public void setStartColour(Color3f c) {
    myLine.setColor(0,c);
  }
  public void setEndColour(Color3f c) {
    myLine.setColor(1,c);
  }
  public ImageIcon getIcon() {
    return new ImageIcon("images/lineEdge.png");
  }
}
