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
package org.wilmascope.forcelayout;

import org.wilmascope.graph.NodeLayout;
import org.wilmascope.graph.EdgeLayout;

import java.util.*;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

/**
 * This class provides a manager or registry of all the available forces
 * which can be added to an instance of ForceLayout.  This class implements
 * the Singleton design pattern (Gamma et al.) such that there can only ever
 * be one instance in the system and a reference to that instance can be
 * obtained by calling the static {link #getInstance()} method from anywhere.
 */
public class ForceManager {
  public static ForceManager getInstance() {
    return instance;
  }
  private ForceManager() {
  }
  public Force createForce(String forceType) {
    Force prototype = (Force)forces.get(forceType);
    return (Force)prototype.clone();
  }
  public void addPrototypeForce(Force prototype) {
    forces.put(prototype.getTypeName(), prototype);
  }
  public NodeLayout createNodeLayout() {
    return new NodeForceLayout();
  }
  public EdgeLayout createEdgeLayout() {
    return new EdgeForceLayout();
  }
  public Collection getAvailableForces() {
    return forces.values();
  }
  private Hashtable forces = new Hashtable();
  private static final ForceManager instance = new ForceManager();
}
