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

import java.awt.Component;
import org.wilmascope.control.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class NodeOptionsMenu extends JPopupMenu implements OptionsClient {
  public NodeOptionsMenu(Component parent, GraphControl.ClusterFacade rootCluster, ControlPanel controlPanel) {
    this();
    this.parent = parent;
    this.rootCluster = rootCluster;
    this.controlPanel = controlPanel;
  }
  public void callback(java.awt.event.MouseEvent e, GraphControl.GraphElementFacade node) {
    this.node = (GraphControl.NodeFacade)node;
    JMenuItem detailsMenuItem;
    if((detailsMenuItem = node.getUserOptionsMenuItem(parent)) != null) {
      remove(this.detailsMenuItem);
      add(detailsMenuItem);
      this.detailsMenuItem = detailsMenuItem;
      //pack();
    }
    show(parent, e.getX(), e.getY());
  }
  JMenuItem addEdgeMenuItem = new JMenuItem();
  JMenuItem deleteMenuItem = new JMenuItem();
  JMenuItem detailsMenuItem = new JMenuItem();

  public NodeOptionsMenu() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    addEdgeMenuItem.setText("Add Edge...");
    addEdgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addEdgeMenuItem_actionPerformed(e);
      }
    });
    deleteMenuItem.setText("Delete");
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteMenuItem_actionPerformed(e);
      }
    });

    detailsMenuItem.setEnabled(false);
    detailsMenuItem.setText("Show Details...");
    addNodeMenuItem.setText("Add Node...");
    addNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addNodeMenuItem_actionPerformed(e);
      }
    });
    setLabelMenuItem.setText("Set Label...");
    setLabelMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setLabelMenuItem_actionPerformed(e);
      }
    });
    hideMenuItem.setText("Hide");
    hideMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideMenuItem_actionPerformed(e);
      }
    });
    setColourMenuItem.setText("Set Colour...");
    setColourMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setColourMenuItem_actionPerformed(e);
      }
    });
    this.add(addNodeMenuItem);
    this.add(addEdgeMenuItem);
    this.add(deleteMenuItem);
    this.add(setLabelMenuItem);
    this.add(setColourMenuItem);
    this.add(hideMenuItem);
    this.add(detailsMenuItem);
  }

  void addEdgeMenuItem_actionPerformed(ActionEvent e) {
    node.highlightColour();
    controlPanel.setMessage("Left click on another node to create an edge...");
    GraphControl.getPickListener().setSinglePickClient(new PickClient() {
      public void callback(GraphControl.GraphElementFacade edgeNode) {
        if(edgeNode!=node) {
          rootCluster.addEdge(node,(GraphControl.NodeFacade)edgeNode);
          rootCluster.unfreeze();
        }
        node.defaultColour();
        controlPanel.setMessage();
      }
    }, GraphControl.nodeClass);
  }

  private Component parent;
  private GraphControl.NodeFacade node;
  private GraphControl.ClusterFacade rootCluster;
  private ControlPanel controlPanel;
  JMenuItem addNodeMenuItem = new JMenuItem();
  JMenuItem setLabelMenuItem = new JMenuItem();
  JMenuItem hideMenuItem = new JMenuItem();
  JMenuItem setColourMenuItem = new JMenuItem();

  void addNodeMenuItem_actionPerformed(ActionEvent e) {
    GraphControl.NodeFacade newNode = rootCluster.addNode();
    newNode.setPosition(node.getPosition());
    rootCluster.addEdge(node,newNode);
    rootCluster.unfreeze();
  }

  void deleteMenuItem_actionPerformed(ActionEvent e) {
    node.delete();
    rootCluster.unfreeze();
  }

  void setLabelMenuItem_actionPerformed(ActionEvent e) {
    controlPanel.setMessage("Opening dialog...");
    String label = JOptionPane.showInputDialog(parent,
      "What would you like to call this node?",
      "Node Label", JOptionPane.QUESTION_MESSAGE);
    controlPanel.setMessage();
    node.setLabel(label);
  }

  void expandMenuItem_actionPerformed(ActionEvent e) {
    ((GraphControl.ClusterFacade)node).expand();
  }

  void hideMenuItem_actionPerformed(ActionEvent e) {
    node.hide();
  }

  void setColourMenuItem_actionPerformed(ActionEvent e) {
    java.awt.Color colour = JColorChooser.showDialog(
      this,"Please select nice colours...", node.getColour());
    if(colour!=null) {
      node.setColour(colour);
    }
  }
}
