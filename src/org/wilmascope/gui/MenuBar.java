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
import javax.swing.event.*;
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
  JMenuItem queryMenuItem = new JMenuItem();
  JMenuItem testMenuItem = new JMenuItem();
  JMenu viewMenu = new JMenu();
  JCheckBoxMenuItem antialiasingCheckBoxMenuItem = new JCheckBoxMenuItem();
  JCheckBoxMenuItem parallelCheckBoxMenuItem = new JCheckBoxMenuItem();
  JCheckBoxMenuItem showMouseHelpCheckBoxMenuItem = new JCheckBoxMenuItem();
  JMenuItem stretchMenuItem = new JMenuItem();
  JMenuItem animationGranularityMenuItem = new JMenuItem();
  JMenuItem backgroundColourMenuItem = new JMenuItem();
  JMenuItem axisPlaneMenuItem = new JMenuItem();
  JMenuItem helpMenuItem = new JMenuItem();
  JMenuItem licenseMenuItem = new JMenuItem();
  JMenuItem aboutMenuItem = new JMenuItem();
  JMenuItem edgeViewMenuItem = new JMenuItem();
  ControlPanel controlPanel;

  public MenuBar(Actions actions, GraphControl graphControl, ControlPanel controlPanel) {
    this.graphControl = graphControl;
    this.controlPanel = controlPanel;
    editMenu = actions.getEditMenu();
    fileMenu = actions.getFileMenu();
    fileMenu.setText("File");
    fileMenu.setMnemonic('F');
    editMenu.setText("Edit");
    editMenu.setMnemonic('E');
    helpMenu.setText("Help");
    helpMenu.setMnemonic('H');
    queryMenuItem.setText("Query");
    queryMenuItem.setMnemonic('Q');
    queryMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        queryMenuItem_actionPerformed(e);
      }
    });
    testMenuItem.setText("Create test graph...");
    testMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        testMenuItem_actionPerformed(e);
      }
    });
    exitMenuItem.setText("Exit");
    exitMenuItem.setMnemonic('x');
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitMenuItem_actionPerformed(e);
      }
    });
    viewMenu.setText("View");
    viewMenu.setMnemonic('V');
    showMouseHelpCheckBoxMenuItem.setText("Show Mouse Help Panel");
    showMouseHelpCheckBoxMenuItem.setMnemonic('M');
    showMouseHelpCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showMouseHelpCheckBoxMenuItem_actionPerformed(e);
      }
    });
    showMouseHelpCheckBoxMenuItem.setState(true);
    antialiasingCheckBoxMenuItem.setText("Antialiasing");
    antialiasingCheckBoxMenuItem.setMnemonic('A');
    antialiasingCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        antialiasingCheckBoxMenuItem_actionPerformed(e);
      }
    });
    parallelCheckBoxMenuItem.setText("Parallel Projection");
    parallelCheckBoxMenuItem.setMnemonic('P');
    parallelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        parallelCheckBoxMenuItem_actionPerformed(e);
      }
    });
    stretchMenuItem.setText("Stretch Vertical Axis...");
    stretchMenuItem.setMnemonic('S');
    stretchMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stretchMenuItem_actionPerformed(e);
      }
    });
    animationGranularityMenuItem.setText("Animation Granularity...");
    animationGranularityMenuItem.setMnemonic('G');
    animationGranularityMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        animationGranularityMenuItem_actionPerformed(e);
      }
    });
    axisPlaneMenuItem.setText("Show axis plane...");
    axisPlaneMenuItem.setMnemonic('x');
    axisPlaneMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        axisPlaneMenuItem_actionPerformed(e);
      }
    });
    backgroundColourMenuItem.setText("Set Background Colour...");
    backgroundColourMenuItem.setMnemonic('B');
    backgroundColourMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        backgroundColourMenuItem_actionPerformed(e);
      }
    });
    helpMenuItem.setText("Help Contents...");
    helpMenuItem.setMnemonic('H');
    helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        helpMenuItem_actionPerformed(e);
      }
    });
    licenseMenuItem.setText("License details...");
    licenseMenuItem.setMnemonic('L');
    licenseMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        licenseMenuItem_actionPerformed(e);
      }
    });
    aboutMenuItem.setText("About");
    aboutMenuItem.setMnemonic('A');
    aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        aboutMenuItem_actionPerformed(e);
      }
    });
    edgeViewMenuItem.setText("Edge View Control...");
    edgeViewMenuItem.setMnemonic('E');
    edgeViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        edgeViewMenuItem_actionPerformed(e);
      }
    });
    this.add(fileMenu);
    this.add(editMenu);
    this.add(viewMenu);
    this.add(helpMenu);
    fileMenu.add(queryMenuItem);
    fileMenu.add(testMenuItem);
    fileMenu.add(exitMenuItem);
    viewMenu.add(showMouseHelpCheckBoxMenuItem);
    viewMenu.add(antialiasingCheckBoxMenuItem);
    viewMenu.add(parallelCheckBoxMenuItem);
    viewMenu.add(stretchMenuItem);
    viewMenu.add(animationGranularityMenuItem);
    viewMenu.add(axisPlaneMenuItem);
    viewMenu.add(backgroundColourMenuItem);
    viewMenu.add(edgeViewMenuItem);
    helpMenu.add(helpMenuItem);
    helpMenu.add(licenseMenuItem);
    helpMenu.add(aboutMenuItem);
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
  JFrame parallelScaleControlFrame = null;
  JFrame stretchControlFrame = null;
  JFrame animationGranularityControlFrame = null;
  void parallelCheckBoxMenuItem_actionPerformed(ActionEvent e) {
    if(parallelCheckBoxMenuItem.isSelected()) {
      graphControl.getGraphCanvas().setParallelProjection(true);
      parallelScaleControlFrame = new JFrame();
      JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL,1,100,10);
      scaleSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          JSlider source = (JSlider)e.getSource();
          graphControl.getGraphCanvas().setScale(0.01 * source.getValue());
        }
      });
      parallelScaleControlFrame.getContentPane().add(scaleSlider);
      parallelScaleControlFrame.setTitle("Adjust Scale...");
      parallelScaleControlFrame.pack();
      parallelScaleControlFrame.show();
    } else {
      graphControl.getGraphCanvas().setParallelProjection(false);
      parallelScaleControlFrame.dispose();
    }
  }
  void stretchMenuItem_actionPerformed(ActionEvent e) {
    stretchControlFrame = new JFrame();
    JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL,1,100,10);
    scaleSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        graphControl.getGraphCanvas().setScale(new javax.vecmath.Vector3d(1,0.1 * source.getValue(),1));
      }
    });
    stretchControlFrame.getContentPane().add(scaleSlider);
    stretchControlFrame.setTitle("Adjust Vertical Scale...");
    stretchControlFrame.pack();
    stretchControlFrame.show();
  }
  void animationGranularityMenuItem_actionPerformed(ActionEvent e) {
    animationGranularityControlFrame = new JFrame();
    JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL,1,100,1);
    scaleSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        graphControl.setIterationsPerFrame(source.getValue());
      }
    });
    animationGranularityControlFrame.getContentPane().add(scaleSlider);
    animationGranularityControlFrame.setTitle("Adjust iterations per frame...");
    animationGranularityControlFrame.pack();
    animationGranularityControlFrame.show();
  }
  void showMouseHelpCheckBoxMenuItem_actionPerformed(ActionEvent e) {
    if(showMouseHelpCheckBoxMenuItem.isSelected()) {
      controlPanel.showMouseHelp();
    } else {
      controlPanel.hideMouseHelp();
    }
  }

  GraphControl graphControl;

  void queryMenuItem_actionPerformed(ActionEvent e) {
    QueryFrame q = new QueryFrame(graphControl);
    q.show();
  }
  void testMenuItem_actionPerformed(ActionEvent e) {
    new org.wilmascope.control.TestGraph(graphControl);
  }
  void helpMenuItem_actionPerformed(ActionEvent e) {
    HelpFrame h = new HelpFrame("../userdoc/index.html");
    h.show();
  }
  void licenseMenuItem_actionPerformed(ActionEvent e) {
    HelpFrame h = new HelpFrame("../userdoc/license.html");
    h.show();
  }
  void aboutMenuItem_actionPerformed(ActionEvent e) {
    new About(null,"images" +java.io.File.separator + "WilmaSplash.png").show();
  }
  void edgeViewMenuItem_actionPerformed(ActionEvent e) {
    new EdgeViewFrame(graphControl, graphControl.getRootCluster()).show();
  }

  void backgroundColourMenuItem_actionPerformed(ActionEvent e) {
    java.awt.Color colour = JColorChooser.showDialog(
      this,"Please select nice colours...", graphControl.getGraphCanvas().getBackgroundColor());
    if(colour!=null) {
      graphControl.getGraphCanvas().setBackgroundColor(colour);
    }
  }
  void axisPlaneMenuItem_actionPerformed(ActionEvent e) {
    if(axisPlaneControlFrame == null) {
      axisPlaneControlFrame = new AxisPlaneControlFrame(graphControl);
      axisPlaneControlFrame.pack();
    }
    axisPlaneControlFrame.show();
  }
  AxisPlaneControlFrame axisPlaneControlFrame;
}
