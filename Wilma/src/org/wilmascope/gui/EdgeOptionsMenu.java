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
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class EdgeOptionsMenu extends JPopupMenu implements OptionsClient {
  JMenuItem deleteMenuItem = new JMenuItem();
  public EdgeOptionsMenu(Component parent, GraphControl.ClusterFacade rootCluster) {
    this();
    this.parent = parent;
    this.rootCluster = rootCluster;
  }
  public void callback(java.awt.event.MouseEvent e, GraphControl.GraphElementFacade edge) {
    this.edge = (GraphControl.EdgeFacade)edge;
    /*
    JMenuItem detailsMenuItem;
    if((detailsMenuItem = edge.getUserOptionsMenuItem(parent)) != null) {
      remove(this.detailsMenuItem);
      add(detailsMenuItem);
      this.detailsMenuItem = detailsMenuItem;
      //pack();
    }
    */
    show(parent, e.getX(), e.getY());
  }
  public EdgeOptionsMenu() {
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
    edgeTypeMenu.setText("Select Type");
    edgeTypeMenu.addMenuListener(new javax.swing.event.MenuListener() {
      public void menuSelected(MenuEvent e) {
        edgeTypeMenu_menuSelected(e);
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuCanceled(MenuEvent e) {
      }
    });
    reverseMenuItem.setText("Reverse Direction");
    reverseMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        reverseMenuItem_actionPerformed(e);
      }
    });
    this.add(deleteMenuItem);
    this.add(hideMenuItem);
    this.add(reverseMenuItem);
    this.add(edgeTypeMenu);
  }

  void deleteMenuItem_actionPerformed(ActionEvent e) {
    edge.delete();
    rootCluster.unfreeze();
  }

  private Component parent;
  private GraphControl.EdgeFacade edge;
  private GraphControl.ClusterFacade rootCluster;
  JMenuItem hideMenuItem = new JMenuItem();
  JMenu edgeTypeMenu = new JMenu();
  JMenuItem reverseMenuItem = new JMenuItem();

  void hideMenuItem_actionPerformed(ActionEvent e) {
    edge.hide();
  }

  void edgeTypeMenu_menuSelected(MenuEvent e) {
    ViewManager.Registry reg = ViewManager.getInstance().getEdgeViewRegistry();
    String[] names=reg.getViewNames();
    edgeTypeMenu.removeAll();
    for(int i=0;i<names.length;i++) {
      final String name = names[i];
      JMenuItem edgeTypeMenuItem = new JMenuItem(name);
      try {
        edgeTypeMenuItem.setIcon(reg.getIcon(name));
      } catch(ViewManager.UnknownViewTypeException ex){
        ex.printStackTrace();
      }
      edgeTypeMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println(name);
          try {
            edge.setView(ViewManager.getInstance().createEdgeView(name));
          } catch (ViewManager.UnknownViewTypeException ex) {
            ex.printStackTrace();
          }
        }
      });
      edgeTypeMenu.add(edgeTypeMenuItem);
    }
  }

  void reverseMenuItem_actionPerformed(ActionEvent e) {
    edge.reverseDirection();
  }
}
