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
package org.wilmascope.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import org.wilmascope.control.*;
/**
 * Defines a control panel for handling the selection of nodes to add
 * to a new or existing cluster
 *
 * @author Tim Dwyer
 */

public class AddToClusterPanel extends MultiPickPanel {

  public AddToClusterPanel(ControlPanel controlPanel, GraphControl.ClusterFacade cluster, GraphControl.ClusterFacade rootCluster) {
    super(controlPanel, cluster);
    this.rootCluster = rootCluster;
  }
  String getLabel() {
    return "Select nodes to add to cluster...";
  }
  void okButton_actionPerformed(ActionEvent e) {
    PickListener pl = GraphControl.getPickListener();
    while(pl.getPickedListSize()>0) {
      GraphControl.GraphElementFacade element = pl.pop();
      cluster.add(element);
    }
    rootCluster.unfreeze();
    cleanup();
  }
  GraphControl.ClusterFacade rootCluster;
}
