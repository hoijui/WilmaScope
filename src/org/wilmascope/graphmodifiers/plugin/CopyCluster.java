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
package org.wilmascope.graphmodifiers.plugin;

import java.util.Hashtable;

import javax.swing.JPanel;

import org.wilmascope.control.GraphControl.Cluster;
import org.wilmascope.control.GraphControl.Edge;
import org.wilmascope.control.GraphControl.Node;
import org.wilmascope.graphmodifiers.GraphModifier;

/**
 * Copy the specified cluster and add the copy/ies to the cluster's owner
 * 
 * @author dwyer
 */
public class CopyCluster extends GraphModifier {

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.util.Plugin#getName()
   */
  public String getName() {
    return "Copy Cluster";
  }

  /*
   * @see org.wilmascope.graphmodifiers.GraphModifier#modify(org.wilmascope.graph.Cluster)
   */
  public void modify(Cluster cluster) {
    Cluster root = null;
    if (cluster.getCluster().getOwner() == null) {
      root = cluster;
    } else {
      root = (Cluster) cluster.getCluster().getOwner().getUserData("Facade");
    }
    Hashtable<Node, Node> mapping = new Hashtable<Node, Node>();
    for (Node n : cluster.getNodes()) {
      mapping.put(n, root.addNode());
    }
    for (Edge e : cluster.getEdges()) {
      root.addEdge(mapping.get(e.getEdge().getStart().getUserData("Facade")), mapping
          .get(e.getEdge().getEnd().getUserData("Facade")));
    }
    cluster.unfreeze();
  }

  /**
   * @see org.wilmascope.util.Plugin#getControls()
   */
  public JPanel getControls() {
    return new JPanel();
  }

}
