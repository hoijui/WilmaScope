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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wilmascope.control.GraphControl.Cluster;
import org.wilmascope.control.GraphControl.Edge;
import org.wilmascope.control.GraphControl.Node;
import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.graphmodifiers.GraphModifier;
import org.wilmascope.gui.SpinnerSlider;

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
	int copyCount = 1;
	
	public CopyCluster(){
		//User Interface
		final SpinnerSlider numberSlider = new SpinnerSlider("Number of copies", 1,
				10, copyCount);
		numberSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				copyCount = numberSlider.getValue();
			}
		});
		controlPanel.add(numberSlider);  	
	}
	
   public String getName() {
		return "Copy Cluster";
	}

  /*
   * @see org.wilmascope.graphmodifiers.GraphModifier#modify(org.wilmascope.graph.Cluster)
   */
   JPanel controlPanel = new JPanel();
   
   public void modify(Cluster cluster) {
   	Cluster root = null;
   	Cluster c1 = null;
   	Hashtable<Node, Node> mapping = new Hashtable<Node, Node>();
   	if (cluster.getCluster().getOwner() == null) {
   		root = cluster;
   		Vector<Node> nodes = new Vector<Node>();
   		for (Node n : cluster.getNodes()) {
   			nodes.add(n);
   		}
   		c1 = root.addCluster(nodes);
   	} else {
   		root = (Cluster)cluster.getCluster().getOwner().getUserData("Facade");
   		c1 = cluster;
   	}
   	for (int i=1;i<=copyCount;i++){
   		Cluster c2 = root.addCluster();
   		for (Node n : c1.getNodes()) {
   			mapping.put(n, c2.addNode());
   		}
   		
   		for (Edge e : c1.getEdges()) {
   			Node a = (Node)e.getEdge().getStart().getUserData("Facade");
   			Node b = (Node)e.getEdge().getEnd().getUserData("Facade");
   			c2.addEdge(mapping.get(a), mapping.get(b));
   		}
   		
   		c1.setLayoutEngine(ForceLayout
   				.createDefaultClusterForceLayout(c1.getCluster()));
   		c1.unfreeze();
   		c2.setLayoutEngine(ForceLayout
   				.createDefaultClusterForceLayout(c2.getCluster()));
   		c2.unfreeze();
   	}
   }
   /**
    * @see org.wilmascope.util.Plugin#getControls()
    */
   public JPanel getControls() {
   	return controlPanel;
   }
   
   
}
