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
package org.wilmascope.graphgen.plugin;

import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wilmascope.control.GraphControl;
import org.wilmascope.graphgen.GraphGenerator;
import org.wilmascope.gui.SpinnerSlider;

/**
 * Generates a random clustered graph with a specified number of nodes and edges
 * with random node positions in either 2 or 3 dimensions.
 * @author dwyer
 */
public class ClusteredGraphGenerator extends GraphGenerator {

  int size = 10;

  int number = 3;

  boolean threeDimensional = true;

  JPanel controlPanel = new JPanel();

  public String getName() {
    return "Clustered";
  }

  /**
   * creates controls
   */
  public ClusteredGraphGenerator() {
    final SpinnerSlider sizeSlider = new SpinnerSlider("Size of clusters", 5, 200,
        size);
    final SpinnerSlider numberSlider = new SpinnerSlider("Number of clusters", 1,
        100, number);
    sizeSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        size = sizeSlider.getValue();
      }
    });
    numberSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        number = numberSlider.getValue();
      }
    });
    controlPanel.add(numberSlider);
    controlPanel.add(sizeSlider);
    final JCheckBox threeDimButton = new JCheckBox("3D Positions");
    threeDimButton.setSelected(threeDimensional);
    threeDimButton.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        threeDimensional = threeDimButton.isSelected();
      }

    });
    controlPanel.add(threeDimButton);
  }

  /**
   * Generates a graph with a given number of clusters of a given size
   * 
   * @see org.wilmascope.graphgen.GraphGenerator#generate(org.wilmascope.control.GraphControl)
   */
  public void generate(GraphControl gc) {
    GraphControl.Cluster root = gc.getRootCluster();
    //LayoutEngine layout = new FastLayout(cluster, threeD);
    //root.setLayoutEngine(layout);

    root.deleteAll();

    Vector allnodes = new Vector();

    for (int j = 0; j < number; j++) { // create each cluster
      Vector nodevec = new Vector();
      GraphControl.Node temp;
      for (int i = 0; i < size; i++) {
        temp = addRandomNode(root,threeDimensional);
        nodevec.add(temp);
        allnodes.add(temp);
      }
      for (int i = 0; i < 2 * size; i++) {
        GraphControl.Node a = (GraphControl.Node) nodevec
            .get(GraphGenerator.getRandom().nextInt(nodevec.size()));
        GraphControl.Node b = (GraphControl.Node) nodevec
            .get(GraphGenerator.getRandom().nextInt(nodevec.size()));
        if (a != b) {
          root.addEdge(a, b, getEdgeView());
        } else {
          i--;
        }
      }
      for (int i = 0; i < nodevec.size(); i++) { // remove all disconnected
                                                 // nodes
        temp = (GraphControl.Node) nodevec.get(i);
        if (temp.getDegree() == 0) {
          temp.delete();
          allnodes.remove(temp);
        }
      }
    }
    for (int i = 0; i < number * (size / 20d); i++) {
      GraphControl.Node a = (GraphControl.Node) allnodes
          .get(GraphGenerator.getRandom().nextInt(allnodes.size()));
      GraphControl.Node b = (GraphControl.Node) allnodes
          .get(GraphGenerator.getRandom().nextInt(allnodes.size()));
      root.addEdge(a, b, getEdgeView());
    }

    gc.getRootCluster().getCluster().draw();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.util.plugin#getControls()
   */
  public JPanel getControls() {
    return controlPanel;
  }

}
