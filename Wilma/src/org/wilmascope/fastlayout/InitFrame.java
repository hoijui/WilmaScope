package org.wilmascope.fastlayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import org.wilmascope.control.*;

/**
 * Description: Window for initialising a FastLayout engine test
 * @author      James Cowling
 * @version     1.0
 */

// this code is rather ugly but does the job and isn't too important

public class InitFrame extends JFrame implements ActionListener {

  String title;

  public static final int RANDOM = 1, GRID = 2, CLUSTER = 3;

  int gen = RANDOM;
  boolean lineRend = true;
  boolean threeD = false;

  JRadioButton randomRadio = new JRadioButton("Random Graph", gen == RANDOM);
  JRadioButton gridRadio = new JRadioButton("Grid", gen == GRID);
  JRadioButton clusterRadio = new JRadioButton("Cluster", gen == CLUSTER);
  ButtonGroup radioGroup = new ButtonGroup();

  JRadioButton lineRadio = new JRadioButton("Lines", lineRend);
  JRadioButton primitiveRadio = new JRadioButton("3D Primitives", !lineRend);
  ButtonGroup radioGroup2 = new ButtonGroup();

  JRadioButton twoDRadio = new JRadioButton("2D", !threeD);
  JRadioButton threeDRadio = new JRadioButton("3D", threeD);
  ButtonGroup radioGroup3 = new ButtonGroup();

  // Variables declaration
  JSlider nodesSlider = new JSlider();
  JSlider edgesSlider = new JSlider();

  Box boxLayout;

  JPanel jPanel1 = new JPanel();

  TestGraph test;

  JButton okButton = new JButton("Generate Graph");

  public InitFrame(String title, TestGraph test) {
    try {
      this.title = title;
      ImageIcon icon = new ImageIcon("images/forces.png");
      this.setIconImage(icon.getImage());

      this.test = test;

      jbInit();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    setTitle (title);
    boxLayout = Box.createVerticalBox();
    this.getContentPane().add(boxLayout, null);

    randomRadio.setActionCommand("random");
    radioGroup.add(randomRadio);
    gridRadio.setActionCommand("grid");
    radioGroup.add(gridRadio);
    clusterRadio.setActionCommand("cluster");
    radioGroup.add(clusterRadio);


    randomRadio.addActionListener(this);
    gridRadio.addActionListener(this);
    clusterRadio.addActionListener(this);

    JPanel radioPanel = new JPanel();
    radioPanel.setLayout(new GridLayout(0, 1));
    radioPanel.setBorder(new TitledBorder("Graph Type"));
    radioPanel.add(randomRadio);
    radioPanel.add(gridRadio);
    radioPanel.add(clusterRadio);

    boxLayout.add(radioPanel);

    lineRadio.setActionCommand("line");
    radioGroup2.add(lineRadio);
    primitiveRadio.setActionCommand("primitive");
    radioGroup2.add(primitiveRadio);

    lineRadio.addActionListener(this);
    primitiveRadio.addActionListener(this);

    JPanel radioPanel2 = new JPanel();
    radioPanel2.setLayout(new GridLayout(0, 1));
    radioPanel2.setBorder(new TitledBorder("Graph Rendering Mode"));
    radioPanel2.add(lineRadio);
    radioPanel2.add(primitiveRadio);

    boxLayout.add(radioPanel2);

    twoDRadio.setActionCommand("2D");
    radioGroup3.add(twoDRadio);
    threeDRadio.setActionCommand("3D");
    radioGroup3.add(threeDRadio);

    twoDRadio.addActionListener(this);
    threeDRadio.addActionListener(this);

    JPanel radioPanel3 = new JPanel();
    radioPanel3.setLayout(new GridLayout(0, 1));
    radioPanel3.setBorder(new TitledBorder("Model Dimension"));
    radioPanel3.add(twoDRadio);
    radioPanel3.add(threeDRadio);

    boxLayout.add(radioPanel3);

    nodesSlider.setBorder (new javax.swing.border.TitledBorder("Number of nodes"));
    nodesSlider.setMaximum(2000);
    nodesSlider.setMinimum(0);
    nodesSlider.setValue(50);
    nodesSlider.setMinorTickSpacing (100);
    nodesSlider.setPaintLabels (true);
    nodesSlider.setPaintTicks (true);
    nodesSlider.setMajorTickSpacing (500);
    boxLayout.add(nodesSlider);

    edgesSlider.setBorder (new javax.swing.border.TitledBorder("Number of edges"));
    edgesSlider.setMaximum(2000);
    edgesSlider.setMinimum(0);
    edgesSlider.setValue(50);
    edgesSlider.setMinorTickSpacing (100);
    edgesSlider.setPaintLabels (true);
    edgesSlider.setPaintTicks (true);
    edgesSlider.setMajorTickSpacing (500);
    boxLayout.add(edgesSlider);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 1));
//    buttonPanel.setBorder(new TitledBorder(""));
    okButton.addActionListener(this);
    okButton.setActionCommand("OK");
    buttonPanel.add(okButton);

    boxLayout.add(buttonPanel); // i should tidy up the heirachy

  }

  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("OK")) {
      if(lineRend) test.setView("LineNode", "LineEdge");
      else test.setView("DefaultNodeView", "Plain Edge");

      if(gen == RANDOM) test.genRandom(nodesSlider.getValue(), edgesSlider.getValue(), threeD);
      else if(gen == GRID) test.genGrid(nodesSlider.getValue(), edgesSlider.getValue(), threeD);
      else test.genClustered(nodesSlider.getValue(), edgesSlider.getValue(), threeD);

      this.hide();
    }
    else if(e.getActionCommand().equals("random")){
      gen = RANDOM;
      nodesSlider.setBorder (new javax.swing.border.TitledBorder("Number of nodes"));
      edgesSlider.setBorder (new javax.swing.border.TitledBorder("Number of edges"));
      nodesSlider.setMaximum(2000);
      nodesSlider.setMinimum(0);
      nodesSlider.setMinorTickSpacing (100);
      nodesSlider.setMajorTickSpacing (500);
      nodesSlider.setPaintLabels (true);
      nodesSlider.setPaintTicks (true);
      nodesSlider.setValue(50);
      nodesSlider.setLabelTable(nodesSlider.createStandardLabels(500,0));
      edgesSlider.setMaximum(2000);
      edgesSlider.setMinimum(0);
      edgesSlider.setMinorTickSpacing (100);
      edgesSlider.setMajorTickSpacing (500);
      edgesSlider.setPaintLabels (true);
      edgesSlider.setPaintTicks (true);
      edgesSlider.setValue(50);
      edgesSlider.setLabelTable(edgesSlider.createStandardLabels(500,0));
    }
    else if(e.getActionCommand().equals("grid")){
      gen = GRID;
      nodesSlider.setBorder (new javax.swing.border.TitledBorder("Grid Width"));
      edgesSlider.setBorder (new javax.swing.border.TitledBorder("Grid Height"));
      nodesSlider.setMaximum(40);
      nodesSlider.setMinimum(0);
      nodesSlider.setMinorTickSpacing (2);
      nodesSlider.setMajorTickSpacing (10);
      nodesSlider.setPaintLabels (true);
      nodesSlider.setPaintTicks (true);
      nodesSlider.setValue(10);
      nodesSlider.setLabelTable(nodesSlider.createStandardLabels(10,0));
      edgesSlider.setMaximum(40);
      edgesSlider.setMinimum(0);
      edgesSlider.setMinorTickSpacing (2);
      edgesSlider.setMajorTickSpacing (10);
      edgesSlider.setPaintLabels (true);
      edgesSlider.setPaintTicks (true);
      edgesSlider.setValue(10);
      edgesSlider.setLabelTable(edgesSlider.createStandardLabels(10,0));
    }
    else if(e.getActionCommand().equals("cluster")){
      gen = CLUSTER;
      nodesSlider.setBorder (new javax.swing.border.TitledBorder("Cluster size"));
      edgesSlider.setBorder (new javax.swing.border.TitledBorder("Number of clusters"));
      nodesSlider.setMaximum(1000);
      nodesSlider.setMinimum(0);
      nodesSlider.setMinorTickSpacing (50);
      nodesSlider.setMajorTickSpacing (250);
      nodesSlider.setPaintLabels (true);
      nodesSlider.setPaintTicks (true);
      nodesSlider.setValue(50);
      nodesSlider.setLabelTable(nodesSlider.createStandardLabels(250,0));
      edgesSlider.setMaximum(20);
      edgesSlider.setMinimum(0);
      edgesSlider.setMinorTickSpacing (2);
      edgesSlider.setMajorTickSpacing (10);
      edgesSlider.setPaintLabels (true);
      edgesSlider.setPaintTicks (true);
      edgesSlider.setValue(2);
      edgesSlider.setLabelTable(edgesSlider.createStandardLabels(10,0));
    }
    else if(e.getActionCommand().equals("line")){
      lineRend = true;
    }
    else if(e.getActionCommand().equals("primitive")){
      lineRend = false;
    }
    else if(e.getActionCommand().equals("2D")){
      threeD = false;
    }
    else if(e.getActionCommand().equals("3D")){
      threeD = true;
    }
  }

}