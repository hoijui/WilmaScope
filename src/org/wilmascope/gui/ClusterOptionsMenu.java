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

import javax.swing.*;
import java.awt.event.*;

import java.awt.Component;
import org.wilmascope.control.*;
import org.wilmascope.view.ViewManager;
import java.util.Vector;
import javax.swing.event.*;

/**
 * A popup menu for clusters
 * @author Tim Dwyer
 */

public class ClusterOptionsMenu extends JPopupMenu implements OptionsClient {
  JMenuItem deleteMenuItem = new JMenuItem();
  public ClusterOptionsMenu(Component parent, GraphControl.ClusterFacade rootCluster, ControlPanel controlPanel) {
    this();
    this.rootCluster = rootCluster;
    this.parent = parent;
    this.controlPanel = controlPanel;
  }
  public void callback(java.awt.event.MouseEvent e, GraphControl.GraphElementFacade cluster) {
    this.cluster = (GraphControl.ClusterFacade)cluster;
    if(this.cluster.isExpanded()) {
      remove(expandMenuItem);
      add(collapseMenuItem);
    } else {
      remove(collapseMenuItem);
      add(expandMenuItem);
    }
    pack();
    show(parent, e.getX(), e.getY());
    updateUI();
  }
  public ClusterOptionsMenu() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    deleteMenuItem.setText("Delete");
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteMenuItem_actionPerformed(e);
      }
    });
    hideMenuItem.setText("Hide");
    hideMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideMenuItem_actionPerformed(e);
      }
    });
    clusterTypeMenu.setText("Select Type");
    clusterTypeMenu.addMenuListener(new javax.swing.event.MenuListener() {
      public void menuSelected(MenuEvent e) {
        clusterTypeMenu_menuSelected(e);
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuCanceled(MenuEvent e) {
      }
    });
    contentsPickingMenuItem.setText("Pick Contents");
    contentsPickingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        contentsPickingMenuItem_actionPerformed(e);
      }
    });
    adjustForcesMenuItem.setText("Adjust Cluster Forces...");
    adjustForcesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adjustForcesMenuItem_actionPerformed(e);
      }
    });
    addToMenuItem.setText("Add to Cluster...");
    addToMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addToMenuItem_actionPerformed(e);
      }
    });
    removeFromMenuItem.setText("Remove from Cluster...");
    removeFromMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeFromMenuItem_actionPerformed(e);
      }
    });
    collapseMenuItem.setText("Collapse Cluster");
    collapseMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        collapseMenuItem_actionPerformed(e);
      }
    });
    expandMenuItem.setText("Expand Cluster");
    expandMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        expandMenuItem_actionPerformed(e);
      }
    });
    setLabelMenuItem.setText("Set Label...");
    setLabelMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setLabelMenuItem_actionPerformed(e);
      }
    });
    this.add(deleteMenuItem);
    this.add(hideMenuItem);
    this.add(contentsPickingMenuItem);
    this.add(addToMenuItem);
    this.add(removeFromMenuItem);
    this.add(setLabelMenuItem);
    this.add(clusterTypeMenu);
    this.addSeparator();
    this.add(adjustForcesMenuItem);
  }

  void deleteMenuItem_actionPerformed(ActionEvent e) {
    cluster.delete();
    cluster.unfreeze();
  }

  private Component parent;
  private GraphControl.ClusterFacade cluster;
  JMenuItem hideMenuItem = new JMenuItem();
  JMenu clusterTypeMenu = new JMenu();
  JMenuItem contentsPickingMenuItem = new JMenuItem();
  JMenuItem adjustForcesMenuItem = new JMenuItem();
  JMenuItem addToMenuItem = new JMenuItem();
  JMenuItem removeFromMenuItem = new JMenuItem();

  void hideMenuItem_actionPerformed(ActionEvent e) {
    cluster.hide();
  }

  void clusterTypeMenu_menuSelected(MenuEvent e) {
    ViewManager.Registry reg = ViewManager.getInstance().getNodeViewRegistry();
    String[] names=reg.getViewNames();
    clusterTypeMenu.removeAll();
    for(int i=0;i<names.length;i++) {
      final String name = names[i];
      JMenuItem clusterTypeMenuItem = new JMenuItem(name);
      clusterTypeMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println(name);
          try {
            cluster.setView(ViewManager.getInstance().createNodeView(name));
          } catch (ViewManager.UnknownViewTypeException ex) {
            ex.printStackTrace();
          }
        }
      });
      clusterTypeMenu.add(clusterTypeMenuItem);
    }
  }

  void contentsPickingMenuItem_actionPerformed(ActionEvent e) {
    cluster.childrenPickable();
  }

  void adjustForcesMenuItem_actionPerformed(ActionEvent e) {
    ForceControlsFrame forceControls = new ForceControlsFrame(cluster,"Force Controls");
    forceControls.show();
  }

  void addToMenuItem_actionPerformed(ActionEvent e) {
    controlPanel.add(new AddToClusterPanel(controlPanel, cluster));
    controlPanel.updateUI();
    cluster.makeNonChildrenPickable();
    GraphControl.getPickListener().enableMultiPicking(100, new Class[]{GraphControl.nodeClass,GraphControl.clusterClass});
  }

  void removeFromMenuItem_actionPerformed(ActionEvent e) {
    controlPanel.add(new RemoveFromClusterPanel(controlPanel, cluster, rootCluster));
    controlPanel.updateUI();
    cluster.makeJustChildrenPickable();
    GraphControl.getPickListener().enableMultiPicking(100, new Class[]{GraphControl.nodeClass, GraphControl.clusterClass});
  }
  ControlPanel controlPanel;
  JMenuItem collapseMenuItem = new JMenuItem();

  void collapseMenuItem_actionPerformed(ActionEvent e) {
    cluster.collapse();
    cluster.unfreeze();
  }
  boolean expanded = true;
  JMenuItem expandMenuItem = new JMenuItem();
  JMenuItem setLabelMenuItem = new JMenuItem();

  void expandMenuItem_actionPerformed(ActionEvent e) {
    cluster.expand();
    cluster.unfreeze();
  }

  void setLabelMenuItem_actionPerformed(ActionEvent e) {
    controlPanel.setMessage("Opening dialog...");
    String label = JOptionPane.showInputDialog(parent,
      "What would you like to call this node?",
      "Node Label", JOptionPane.QUESTION_MESSAGE);
    controlPanel.setMessage();
    cluster.setLabel(label);
  }
  private GraphControl.ClusterFacade rootCluster;
}
