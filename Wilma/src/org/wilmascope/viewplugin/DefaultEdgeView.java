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
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Cone;

import com.sun.j3d.utils.picking.PickTool;

/**
 * Graphical representation of the edge
 *
 * @author Tim Dwyer
 * @version 1.0
 */

public class DefaultEdgeView extends EdgeView {
  /** radius of the edgeCylinder
   */
  float radius = 0.02f;
  public DefaultEdgeView() {
    setTypeName("Plain Edge");
  }
  protected void setupDefaultMaterial() {
    Material material = new Material();
    material.setDiffuseColor(0.0f, 0.0f, 1.0f);
    material.setAmbientColor(0f,0f,0.4f);
    material.setShininess(50.0f);
    setupDefaultAppearance(material);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void init() {
/*    Cylinder edgeCylinder = new Cylinder(radius,1,Cylinder.GENERATE_NORMALS,10,1,getAppearance());
    makePickable(edgeCylinder.getShape(Cylinder.BODY));
    makePickable(edgeCylinder.getShape(Cylinder.TOP));
    makePickable(edgeCylinder.getShape(Cylinder.BOTTOM));
	addTransformGroupChild(edgeCylinder);
        */
    	Switch sw = new Switch(0);
	sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_READ);
	sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_WRITE);


	// Create several levels for the switch, with less detailed
	// spheres for the ones which will be used when the sphere is
	// further away
        Cylinder one = new Cylinder(radius, 1, Cylinder.GENERATE_NORMALS, 10, 1, getAppearance());
        Cylinder two = new Cylinder(radius, 1, Cylinder.GENERATE_NORMALS, 6, 1, getAppearance());
        Cylinder three = new Cylinder(radius, 1, Cylinder.GENERATE_NORMALS, 3, 1, getAppearance());
        // Bizarrely when you only have 1 segment along the height of the
        // cylinder you have to set the picking capability bits for top, bottom
        // and body just to pick it from the side
        makePickable(one.getShape(Cylinder.BOTTOM));
        makePickable(two.getShape(Cylinder.BOTTOM));
        makePickable(three.getShape(Cylinder.BOTTOM));
        makePickable(one.getShape(Cylinder.TOP));
        makePickable(two.getShape(Cylinder.TOP));
        makePickable(three.getShape(Cylinder.TOP));
        makePickable(one.getShape(Cylinder.BODY));
        makePickable(two.getShape(Cylinder.BODY));
        makePickable(three.getShape(Cylinder.BODY));
	sw.addChild(one);
	sw.addChild(two);
	sw.addChild(three);

	// Add the switch to the main group
	addTransformGroupChild(sw);

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// set up the DistanceLOD behavior
	float[] distances = new float[2];
	distances[0] = 5.0f;
	distances[1] = 10.0f;
	DistanceLOD lod = new DistanceLOD(distances);
	lod.addSwitch(sw);
	lod.setSchedulingBounds(bounds);
	addTransformGroupChild(lod);
  }
}
