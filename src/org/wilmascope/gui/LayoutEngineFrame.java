package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import org.wilmascope.control.GraphControl;
import org.wilmascope.graph.Cluster;
import org.wilmascope.forcelayout.ForceLayout;
import java.awt.event.*;

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
    if(layoutEngine.getClass().getName().equals("org.wilmascope.forcelayout.ForceLayout")) {
      controlsPanel = new ForceControlsPanel(cluster);
    } else if(layoutEngine.getClass().getName().equals("org.wilmascope.fastlayout.FastLayout")) {
      controlsPanel = new org.wilmascope.fastlayout.ParamsPanel(cluster);
    } else if(layoutEngine.getClass().getName().equals("org.wilmascope.multiscalelayout.MultiScaleLayout")) {
      controlsPanel = new org.wilmascope.multiscalelayout.MSParamsPanel(cluster);
    }
    this.cluster = cluster;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
  }
  private void jbInit() throws Exception {
    ImageIcon icon = new ImageIcon(getClass().getResource("/images/forces.png"));
    this.setIconImage(icon.getImage());
    box1 = Box.createVerticalBox();
    layoutEngineComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutEngineComboBox_actionPerformed(e);
      }
    });
    this.getContentPane().add(box1, BorderLayout.CENTER);
    box1.add(jPanel1, null);
    box1.add(controlsPanel);
    jPanel1.add(layoutEngineComboBox, null);
  }

  void layoutEngineComboBox_actionPerformed(ActionEvent e) {
    String s = (String)layoutEngineComboBox.getSelectedItem();
    if(s.equals(layoutEngineName)) {
      return;
    }
    layoutEngineName = s;
    box1.remove(controlsPanel);
    Cluster c = cluster.getCluster();
    if(layoutEngineName.equals("Force Directed")) {
      org.wilmascope.forcelayout.ForceLayout forcelayout = new org.wilmascope.forcelayout.ForceLayout(c);
      c.setLayoutEngine(forcelayout);
      forcelayout.createElementLayouts();
      controlsPanel = new ForceControlsPanel(cluster);
    } else if(layoutEngineName.equals("Simulated Annealing")) {
      org.wilmascope.fastlayout.FastLayout fastlayout = new org.wilmascope.fastlayout.FastLayout(c, true);
      c.setLayoutEngine(fastlayout);
      controlsPanel = new org.wilmascope.fastlayout.ParamsPanel(cluster);
    } else if(layoutEngineName.equals("Multi-Scale")) {
      org.wilmascope.multiscalelayout.MultiScaleLayout multiscalelayout = new org.wilmascope.multiscalelayout.MultiScaleLayout(c);
      c.setLayoutEngine(multiscalelayout);
      controlsPanel = new org.wilmascope.multiscalelayout.MSParamsPanel(cluster);
    }
    box1.add(controlsPanel);
    pack();
  }
}