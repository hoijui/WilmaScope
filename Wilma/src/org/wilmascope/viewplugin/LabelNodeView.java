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
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Sphere;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class LabelNodeView extends NodeView {
  public LabelNodeView() {
    setTypeName("LabelOnly");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.redMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void setLabel(String label) {
    float leftShift = -0.02f * (float)label.length();
    addLabel(label, 0.04d, new Point3f(-leftShift,0.03f,0.0f), new Vector3f(leftShift,-0.03f,0.0f), getAppearance());
  }
  protected void init() {
    // now create a fully transparent pickable sphere to encompass the label
    // so user has something to pick.
    Appearance transApp = new Appearance();
    transApp.setTransparencyAttributes(
      new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.99f));
    Sphere pickableSphere = new Sphere(0.2f, Sphere.GENERATE_NORMALS, 5, transApp);
    makePickable(pickableSphere.getShape(Sphere.BODY));
    addTransformGroupChild(pickableSphere);
  }
}
