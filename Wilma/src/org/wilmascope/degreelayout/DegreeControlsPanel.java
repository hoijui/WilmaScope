/*
 * Created on Jun 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.degreelayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import javax.swing.border.*;
//import java.util.Vector;
//import org.wilmascope.forcelayout.*;
import org.wilmascope.control.GraphControl;

/**
 * @author cmurray
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

public class DegreeControlsPanel extends JPanel {

  public DegreeControlsPanel() {
  }

  DegreeLayout degreeLayout;

  GraphControl.ClusterFacade cluster;

  // Variables declaration
  //JSlider velocityAttenuationSlider = new JSlider();
  //JSlider frictionSlider = new JSlider();
  //JSlider iterationsPerFrameSlider = new JSlider();
  JScrollPane jScrollPane1 = new JScrollPane();

  //JScrollPane jScrollPane2 = new JScrollPane();

  Box boxLayout;

  //Box fadeLayoutControlsBox;
  Box degreeControlsBox;

  JPanel jPanel1 = new JPanel();

  JSlider balancedThresholdSlider = new JSlider();

  public DegreeControlsPanel(GraphControl.ClusterFacade cluster) {
    this.cluster = cluster;
    this.degreeLayout = (DegreeLayout) cluster.getLayoutEngine();
    boxLayout = Box.createVerticalBox();
    degreeControlsBox = Box.createVerticalBox();
    //forceLayoutControlsBox = Box.createVerticalBox();
    this.add(boxLayout, BorderLayout.CENTER);
    boxLayout.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(degreeControlsBox, null);
    degreeControlsBox.add(jPanel1, null);
    /*
     * boxLayout.add(jScrollPane2, null);
     * jScrollPane2.getViewport().add(forceLayoutControlsBox, null);
     * velocityAttenuationSlider.setBorder (new
     * javax.swing.border.TitledBorder("Velocity Scale"));
     * velocityAttenuationSlider.setMinorTickSpacing (10);
     * velocityAttenuationSlider.setPaintLabels (true);
     * velocityAttenuationSlider.setPaintTicks (true);
     * velocityAttenuationSlider.setMajorTickSpacing (50);
     * //velocityAttenuationSlider.setValue
     * ((int)((float)fadeLayout.getVelocityAttenuation() * 1000f));
     * velocityAttenuationSlider.addChangeListener (new
     * javax.swing.event.ChangeListener () { public void stateChanged
     * (javax.swing.event.ChangeEvent evt) {
     * //fadeLayout.setVelocityAttenuation((float)velocityAttenuationSlider.getValue()/1000f); }
     * }); forceLayoutControlsBox.add(velocityAttenuationSlider);
     * frictionSlider.setBorder (new javax.swing.border.TitledBorder("Friction
     * Coefficient")); frictionSlider.setMinorTickSpacing (10);
     * frictionSlider.setPaintLabels (true); frictionSlider.setPaintTicks
     * (true); frictionSlider.setMajorTickSpacing (50);
     * //frictionSlider.setValue ((int)((float)Constants.frictionCoefficient));
     * frictionSlider.addChangeListener (new javax.swing.event.ChangeListener () {
     * public void stateChanged (javax.swing.event.ChangeEvent evt) {
     * //Constants.frictionCoefficient = (float)frictionSlider.getValue(); } });
     * forceLayoutControlsBox.add(frictionSlider);
     * balancedThresholdSlider.setBorder (new
     * javax.swing.border.TitledBorder("Balanced Threshold"));
     * balancedThresholdSlider.setMinorTickSpacing (10);
     * balancedThresholdSlider.setPaintLabels (true);
     * balancedThresholdSlider.setPaintTicks (true);
     * balancedThresholdSlider.setMajorTickSpacing (50);
     * //balancedThresholdSlider.setValue
     * ((int)((float)fadeLayout.getBalancedThreshold() * 1000f));
     * balancedThresholdSlider.addChangeListener (new
     * javax.swing.event.ChangeListener () { public void stateChanged
     * (javax.swing.event.ChangeEvent evt) {
     * //fadeLayout.setBalancedThreshold((float)balancedThresholdSlider.getValue()/1000f); }
     * }); forceLayoutControlsBox.add(balancedThresholdSlider);
     */

    //java.util.Vector forces = new
    // Vector(ForceManager.getInstance().getAvailableForces());
    //for(int i=0; i<forces.size(); i++) {
    degreeControlsBox.add(new DegreeEdgeControlPanel(cluster));
    degreeControlsBox.add(new DegreeNodeControlPanel(cluster));
    degreeControlsBox.add(new DegreeOriginControlPanel(cluster));
    //}

    Box degreeBox = Box.createVerticalBox();
    degreeBox.setBorder(BorderFactory.createTitledBorder(""));

    JCheckBox degreeCheckBox = new JCheckBox("In-Degree Only");

    degreeBox.add(degreeCheckBox);

    degreeControlsBox.add(degreeBox);

    if (VariableForces.inOnly) {
      degreeCheckBox.setSelected(true);
    }
    degreeCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        changedDegreeCheckBox_actionPerformed(e);
      }
    });
  }

  void changedDegreeCheckBox_actionPerformed(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.DESELECTED) {

      VariableForces.inOnly = false;
    } else {

      VariableForces.inOnly = true;
    }
    cluster.unfreeze();
  }
}
