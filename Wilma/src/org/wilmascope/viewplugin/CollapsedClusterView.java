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

import org.wilmascope.view.ClusterView;
import org.wilmascope.view.Colours;
import org.wilmascope.view.LODSphere;
/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope organisation
 * (www.wilmascope.org) however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you site
 * WilmaScope as an influence and inform us (tgdwyer@yahoo.com)
 * if you do anything really cool with it.
 *
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma
 *
 * -- Tim Dwyer, 2001
 */

public class CollapsedClusterView extends ClusterView {

  public CollapsedClusterView() {
    setTypeName("CollapsedClusterView");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.pinkMaterial);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void init() {
    //Sphere sphere = new Sphere(1.0f,Sphere.GEOMETRY_NOT_SHARED|Sphere.GENERATE_NORMALS,null);
    //addShape(new Shape3D(sphere.getShape().getGeometry()));
    LODSphere sphere = new LODSphere(1.0f,getAppearance());
    sphere.makePickable(this);
    sphere.addToTransformGroup(getTransformGroup());

    org.wilmascope.graph.Cluster c = (org.wilmascope.graph.Cluster)getNode();
    float r = (float)Math.pow(3.0f*c.getMass()/(4.0f*Math.PI),1.0d/3.0d)/4.0f;
    c.setRadius(r);
  }
}
