package org.wilmascope.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.wilmascope.control.GraphControl;
import org.wilmascope.control.GraphTransformer;
import org.wilmascope.forcelayout.ForceLayout;


/**
 * Controls for clustering.
 * 
 * @author dwyer
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
    pack();
  }

  void okButton_actionPerformed(ActionEvent e) {
    GraphTransformer transformer = new GraphTransformer();
    int k = Integer.parseInt(kField.getText());
    int n = Integer.parseInt(keepField.getText());
    Vector[] clusters = transformer.kMeansClustering(rootCluster, k, n);
    for(int i = 0; i < clusters.length; i++) {
      GraphControl.ClusterFacade newCluster = rootCluster.addCluster(clusters[i]);
      newCluster.setLayoutEngine(ForceLayout.createDefaultClusterForceLayout(newCluster.getCluster()));
      if(!expandedCheckBox.isSelected()) {
        newCluster.collapse();
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