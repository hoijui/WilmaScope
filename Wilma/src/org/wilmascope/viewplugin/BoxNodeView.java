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
import com.sun.j3d.utils.geometry.Text2D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.awt.Font;
import javax.swing.ImageIcon;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class BoxNodeView extends NodeView {
  public BoxNodeView() {
    setTypeName("Box");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.defaultMaterial);
    getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);
    getAppearance().setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  protected void init() {
    box = new LabelCube(getAppearance(), getNode().getRadius(), 1.0f);
    Appearance app = new Appearance();
    app.setMaterial(getAppearance().getMaterial());
    border = new NoLabelCube(app, getNode().getRadius(), 1.0f);
    makePickable(border);
    addTransformGroupChild(border);
    makePickable(box);
    addTransformGroupChild(box);
  }
  public void setLabel(String label) {
    Appearance appearance = getAppearance();
    Text2D textObject = new Text2D(label,
    org.wilmascope.view.Colours.black,
    "Arial",
    55,
    Font.PLAIN);
    TextureAttributes texAttr = new TextureAttributes();
    texAttr.setTextureMode(TextureAttributes.DECAL);
    appearance.setTextureAttributes(texAttr);

    appearance.setTexture(textObject.getAppearance().getTexture());

    // Create a label cube wide enough to comfortably fit the text */
    Point3d coord = new Point3d();
    QuadArray qa = (QuadArray)textObject.getGeometry();
    // First coordinate in the GeometryArray of the textObject should
    // be the top right corner so use its x value to determine the width
    qa.getCoordinate(0,coord);
    float width = getNode().getRadius(), widthScale;
    // 3.0 is the maximum scaling factor, 1.0 is the minimum
    if(coord.x>2.1d) {
      widthScale = 3.0f;
    } else if(coord.x<1.0d) {
      widthScale = 1.0f;
    } else {
      widthScale = (float)coord.x;
    }
    //getNode().setRadius(width);
    box.generateGeometry(width, widthScale);
    border.generateGeometry(width, widthScale);
  }
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("/images/cube.png"));
  }
  LabelCube box;
  NoLabelCube border;

}
