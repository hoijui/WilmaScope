package org.wilmascope.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.wilmascope.control.GraphControl;
import org.wilmascope.dotlayout.DotLayout;
import org.wilmascope.fastlayout.FastLayout;
import org.wilmascope.forcelayout.*;
import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.multiscalelayout.MultiScaleLayout;

/**
 * @author dwyer
 *
 * A frame with a panel for selecting the layout engine, and a panel
 * for controls specific to that engine
 */
public class LayoutEngineFrame extends JFrame {
  private Box box1;
  private JPanel jPanel1 = new JPanel();
  private JComboBox layoutEngineComboBox = new JComboBox(new String[]{"Force Directed","Simulated Annealing","Multi-Scale","Dot Layered"});
  private JPanel controlsPanel;
  private String layoutEngineName;
  GraphControl.ClusterFacade cluster;
  public LayoutEngineFrame(GraphControl.ClusterFacade cluster, String title) {
    setTitle (title);
    org.wilmascope.graph.LayoutEngine layoutEngine = cluster.getLayoutEngine();
    controlsPanel=layoutEngine.getControls();
    this.cluster = cluster;
    ImageIcon icon = new ImageIcon("images/forces.png");
    this.setIconImage(icon.getImage());
    box1 = Box.createVerticalBox();
    layoutEngineComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutEngineComboBox_actionPerformed(e);
      }
    });
    if(layoutEngine instanceof org.wilmascope.forcelayout.ForceLayout) {
      layoutEngineComboBox.setSelectedIndex(0);
    } else if(layoutEngine instanceof DotLayout) {
      layoutEngineComboBox.setSelectedIndex(3);
    } else if(layoutEngine instanceof MultiScaleLayout) {
      layoutEngineComboBox.setSelectedIndex(2);
    } else if(layoutEngine instanceof FastLayout) {
      layoutEngineComboBox.setSelectedIndex(1);
    }
    this.getContentPane().add(box1, BorderLayout.CENTER);
    box1.add(jPanel1, null);
    box1.add(controlsPanel);
    jPanel1.add(layoutEngineComboBox, null);
    pack();
  }

  void layoutEngineComboBox_actionPerformed(ActionEvent e) {
    String s = (String)layoutEngineComboBox.getSelectedItem();
    if(s.equals(layoutEngineName)) {
      return;
    }
    layoutEngineName = s;
    box1.remove(controlsPanel);
    Cluster c = cluster.getCluster();
    LayoutEngine layoutEngine=null;
    if(layoutEngineName.equals("Force Directed")) {
      layoutEngine = new org.wilmascope.forcelayout.ForceLayout(c);
      c.setLayoutEngine(layoutEngine);
      ((ForceLayout)layoutEngine).createElementLayouts();
    } else if(layoutEngineName.equals("Simulated Annealing")) {
      layoutEngine = new org.wilmascope.fastlayout.FastLayout(c, true);
      c.setLayoutEngine(layoutEngine);
    } else if(layoutEngineName.equals("Multi-Scale")) {
      layoutEngine = new org.wilmascope.multiscalelayout.MultiScaleLayout(c);
      c.setLayoutEngine(layoutEngine);
    } else if(layoutEngineName.equals("Dot Layered")) {
      layoutEngine = new DotLayout(c);
      c.setLayoutEngine(layoutEngine);
    }
    controlsPanel = layoutEngine.getControls();
    box1.add(controlsPanel);
    pack();
  }
}