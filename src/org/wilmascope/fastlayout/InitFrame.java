package org.wilmascope.fastlayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.Vector;
import org.wilmascope.fastlayout.*;
import org.wilmascope.control.*;

/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */


 /*
  *
  *
  * Really ugly hacked together code at the moment
  *
  * Most of it was just stolen from Tim's code and mistreated :)
  *
  *
  */


public class InitFrame extends JFrame implements ActionListener {

  FastLayout layout;
  GraphControl.ClusterFacade cluster;
  String title;


  // Variables declaration
  JSlider nodesSlider = new JSlider(0, 1000, 50);
  JSlider edgesSlider = new JSlider(0, 1000, 50);

  Box boxLayout;

  JPanel jPanel1 = new JPanel();

  TestGraph test;

  JButton okButton = new JButton("Let's go shopping...");

  public InitFrame(GraphControl.ClusterFacade cluster, String title, TestGraph test) {
    try {
      this.title = title;
      this.cluster = cluster;
      this.layout = (FastLayout)cluster.getLayoutEngine();
      ImageIcon icon = new ImageIcon(getClass().getResource("/images/forces.png"));
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

    nodesSlider.setBorder (new javax.swing.border.TitledBorder("Number of nodes"));
    nodesSlider.setMinorTickSpacing (100);
    nodesSlider.setPaintLabels (true);
    nodesSlider.setPaintTicks (true);
    nodesSlider.setMajorTickSpacing (500);
    boxLayout.add(nodesSlider);

    edgesSlider.setBorder (new javax.swing.border.TitledBorder("Number of edges"));
    edgesSlider.setMinorTickSpacing (100);
    edgesSlider.setPaintLabels (true);
    edgesSlider.setPaintTicks (true);
    edgesSlider.setMajorTickSpacing (500);
    boxLayout.add(edgesSlider);

    okButton.addActionListener(this);
    okButton.setActionCommand("OK");

    boxLayout.add(okButton); // i should tidy up the heirachy

  }

  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("OK")) {
      test.genRandom(nodesSlider.getValue(), edgesSlider.getValue());
      this.hide();
    }
  }

}