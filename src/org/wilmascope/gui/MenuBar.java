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
import java.awt.MenuItem;

import org.wilmascope.control.GraphControl;
import java.awt.event.*;
import java.util.Vector;

/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */


public class MenuBar extends JMenuBar {
  JMenu fileMenu;
  JMenu helpMenu = new JMenu();
  JMenu editMenu;
  JMenuItem exitMenuItem = new JMenuItem();
  JMenu viewMenu = new JMenu();
  JCheckBoxMenuItem antialiasingCheckBoxMenuItem = new JCheckBoxMenuItem();

  public MenuBar(Actions actions, GraphControl graphControl, ControlPanel controlPanel) {
    this.graphControl = graphControl;
    editMenu = actions.getEditMenu();
    fileMenu = actions.getFileMenu();
    fileMenu.setText("File");
    fileMenu.setMnemonic('F');
    editMenu.setText("Edit");
    editMenu.setMnemonic('E');
    helpMenu.setText("Help");
    helpMenu.setMnemonic('H');
    exitMenuItem.setText("Exit");
    exitMenuItem.setMnemonic('x');
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitMenuItem_actionPerformed(e);
      }
    });
    viewMenu.setText("View");
    viewMenu.setMnemonic('V');
    antialiasingCheckBoxMenuItem.setText("Antialiasing");
    antialiasingCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        antialiasingCheckBoxMenuItem_actionPerformed(e);
      }
    });
    this.add(fileMenu);
    this.add(editMenu);
    this.add(viewMenu);
    this.add(helpMenu);
    fileMenu.add(exitMenuItem);
    viewMenu.add(antialiasingCheckBoxMenuItem);
  }

  void exitMenuItem_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  void antialiasingCheckBoxMenuItem_actionPerformed(ActionEvent e) {
    if(antialiasingCheckBoxMenuItem.isSelected()) {
      graphControl.getGraphCanvas().setAntialiasingEnabled(true);
    } else {
      graphControl.getGraphCanvas().setAntialiasingEnabled(false);
    }
  }
  GraphControl graphControl;

}
