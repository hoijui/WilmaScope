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

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.geometry.Sphere;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class DefaultNodeView extends NodeView {
  public DefaultNodeView() {
    setTypeName("DefaultNodeView");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.redMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public javax.swing.JMenuItem getUserOptionsMenuItem(java.awt.Component parent) {
    javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem();
    menuItem.setText("Show Details...");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        menuItemActionPerformed();
      }
    });
    return menuItem;
  }
  public void menuItemActionPerformed() {
    DetailFrame details = new DetailFrame(this, "text/html",text);
    details.show();
  }
  public void setData(String text) {
    this.text = text;
  }
  public String getData() {
    return text;
  }
  protected void init() {
    /*
    Sphere sphere = new Sphere(1.0f,Sphere.GENERATE_NORMALS,10,getAppearance());
    makePickable(sphere.getShape());
    addTransformGroupChild(sphere);
    */
    LODSphere sphere = new LODSphere(getAppearance());
    sphere.makePickable(this);
    sphere.addToTransformGroup(getTransformGroup());
  }
  private String text = new String();
}
