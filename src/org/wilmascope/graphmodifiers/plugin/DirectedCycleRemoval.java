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

import javax.swing.JPanel;

import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;
import org.wilmascope.graphmodifiers.GraphModifier;

/**
 * Removes directed cycles
 * 
 * @author dwyer
 */
public class DirectedCycleRemoval extends GraphModifier {
  JPanel controls;

  public DirectedCycleRemoval() {
    controls = new JPanel();
  }

  /**
   * @see org.wilmascope.graphmodifiers.GraphModifier#getName()
   */
  public String getName() {
    return "Directed Cycle Removal";
  }

  /**
   * @see org.wilmascope.graphmodifiers.GraphModifier#modify(org.wilmascope.graph.Cluster)
   */
  public void modify(Cluster cluster) {
    for (Edge e : getAcyclicEdgeSet_Greedy(cluster)) {
      e.reverseDirection();
    }
    cluster.draw();
  }

  EdgeList getAcyclicEdgeSet_Greedy(Cluster cluster) {
    return cluster.getAcyclicEdgeSet_Greedy();
  }

  EdgeList getAcyclicEdgeSet_EnhancedGreedy(Cluster cluster) {
    return cluster.getAcyclicEdgeSet_EnhancedGreedy();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.graphmodifiers.GraphModifier#getControls()
   */
  public JPanel getControls() {
    return controls;
  }

}
