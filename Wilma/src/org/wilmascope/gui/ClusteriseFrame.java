package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import org.wilmascope.control.*;
import org.wilmascope.graph.NodeList;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ClusteriseFrame extends JFrame {
  JTabbedPane ClusterChoicePane = new JTabbedPane();
  Box kMeansBox;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JTextField kField = new JTextField();
  JPanel jPanel2 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JTextField keepField = new JTextField();

  public ClusteriseFrame(GraphControl.ClusterFacade rootCluster, String windowTitle) {
    super(windowTitle);
    this.rootCluster = rootCluster;
    try {
      jbInit();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    kMeansBox = Box.createVerticalBox();
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jLabel1.setText("Clusters to examine (k)");
    kField.setPreferredSize(new Dimension(50, 27));
    kField.setText("10");
    jLabel2.setText("Clusters to keep (<=k)");
    keepField.setPreferredSize(new Dimension(50, 27));
    keepField.setText("5");
    expandedCheckBox.setText("Show Expanded");
    this.getContentPane().add(ClusterChoicePane, BorderLayout.CENTER);
    this.getContentPane().add(jPanel3,  BorderLayout.SOUTH);
    jPanel3.add(okButton, null);
    jPanel3.add(cancelButton, null);
    ClusterChoicePane.add(kMeansBox,    "k-Means");
    kMeansBox.add(jPanel1, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(kField, null);
    kMeansBox.add(jPanel2, null);
    jPanel2.add(jLabel2, null);
    jPanel2.add(keepField, null);
    kMeansBox.add(jPanel4, null);
    jPanel4.add(expandedCheckBox, null);
  }

  void okButton_actionPerformed(ActionEvent e) {
    GraphTransformer transformer = new GraphTransformer();
    int k = Integer.parseInt(kField.getText());
    int n = Integer.parseInt(keepField.getText());
    Vector[] clusters = transformer.kMeansClustering(rootCluster, k, n);
    for(int i = 0; i < clusters.length; i++) {
      GraphControl.ClusterFacade newCluster = rootCluster.addCluster(clusters[i]);

      try {
        newCluster.addForce("Repulsion");
        newCluster.addForce("Spring");
        newCluster.addForce("Origin").setStrength(10f);
        if(!expandedCheckBox.isSelected()) {
          newCluster.collapse();
        }
      } catch(Exception fe) {
        System.out.println("Couldn't add forces to graph root from WilmaMain, reason: "+fe.getMessage());
      }
    }
  }
  GraphControl.ClusterFacade rootCluster;
  JPanel jPanel4 = new JPanel();
  JCheckBox expandedCheckBox = new JCheckBox();

  void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }
}