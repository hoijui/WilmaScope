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
package org.wilmascope.graphanalysis;

import java.awt.Color;

import javax.swing.JPanel;

import org.wilmascope.control.GraphControl.Cluster;
import org.wilmascope.control.GraphControl.Node;
import org.wilmascope.gui.HueSettingsPanel;

/**
 * A mapping for node analysis properties to repulsive mass
 * @author dwyer
 */
public class NodeMassMapping extends VisualMapping {
  public NodeMassMapping() {
    super("Node Repulsion");
  }

  JPanel controls = new JPanel();
  public void apply(Cluster c, String analysisType) {
    for (Node n : c.getNodes()) {
      float attr = Float.parseFloat(n.getProperties().getProperty(
          analysisType));
      n.setMass(attr * 5f+1f);
    }
    c.unfreeze();
  }

  public JPanel getControls() {
    return controls;
  }
}
