/*
 * Created on Jun 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.fadelayout;

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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */


public class FadeControlsPanel extends JPanel {

  public FadeControlsPanel() {
  }
  FadeLayout fadeLayout;
  GraphControl.ClusterFacade cluster;

  // Variables declaration
  //JSlider velocityAttenuationSlider = new JSlider();
  //JSlider frictionSlider = new JSlider();
  //JSlider iterationsPerFrameSlider = new JSlider();
  JScrollPane jScrollPane1 = new JScrollPane();
  //JScrollPane jScrollPane2 = new JScrollPane();

  Box boxLayout;
  //Box fadeLayoutControlsBox;
  Box fadeControlsBox;
  JPanel jPanel1 = new JPanel();
  JSlider balancedThresholdSlider = new JSlider();

  public FadeControlsPanel(GraphControl.ClusterFacade cluster) {
    try {
      this.cluster = cluster;
      this.fadeLayout = (FadeLayout)cluster.getLayoutEngine();
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    boxLayout = Box.createVerticalBox();
    fadeControlsBox = Box.createVerticalBox();
    //forceLayoutControlsBox = Box.createVerticalBox();
    this.add(boxLayout, BorderLayout.CENTER);
    boxLayout.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(fadeControlsBox, null);
    fadeControlsBox.add(jPanel1, null);
    /*boxLayout.add(jScrollPane2, null);
    jScrollPane2.getViewport().add(forceLayoutControlsBox, null);
    velocityAttenuationSlider.setBorder (new javax.swing.border.TitledBorder("Velocity Scale"));
    velocityAttenuationSlider.setMinorTickSpacing (10);
    velocityAttenuationSlider.setPaintLabels (true);
    velocityAttenuationSlider.setPaintTicks (true);
    velocityAttenuationSlider.setMajorTickSpacing (50);
    //velocityAttenuationSlider.setValue ((int)((float)fadeLayout.getVelocityAttenuation() * 1000f));
    velocityAttenuationSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        //fadeLayout.setVelocityAttenuation((float)velocityAttenuationSlider.getValue()/1000f);
      }
    });
    forceLayoutControlsBox.add(velocityAttenuationSlider);
    frictionSlider.setBorder (new javax.swing.border.TitledBorder("Friction Coefficient"));
    frictionSlider.setMinorTickSpacing (10);
    frictionSlider.setPaintLabels (true);
    frictionSlider.setPaintTicks (true);
    frictionSlider.setMajorTickSpacing (50);
    //frictionSlider.setValue ((int)((float)Constants.frictionCoefficient));
    frictionSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        //Constants.frictionCoefficient = (float)frictionSlider.getValue();
      }
    });
    forceLayoutControlsBox.add(frictionSlider);
    balancedThresholdSlider.setBorder (new javax.swing.border.TitledBorder("Balanced Threshold"));
    balancedThresholdSlider.setMinorTickSpacing (10);
    balancedThresholdSlider.setPaintLabels (true);
    balancedThresholdSlider.setPaintTicks (true);
    balancedThresholdSlider.setMajorTickSpacing (50);
    //balancedThresholdSlider.setValue ((int)((float)fadeLayout.getBalancedThreshold() * 1000f));
    balancedThresholdSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        //fadeLayout.setBalancedThreshold((float)balancedThresholdSlider.getValue()/1000f);
      }
    });
    forceLayoutControlsBox.add(balancedThresholdSlider);*/

    //java.util.Vector forces = new Vector(ForceManager.getInstance().getAvailableForces());
    //for(int i=0; i<forces.size(); i++) {
      fadeControlsBox.add(new FadeEdgeControlPanel(cluster));
      fadeControlsBox.add(new FadeNodeControlPanel(cluster));
      fadeControlsBox.add(new FadeOriginControlPanel(cluster));
      
      Box layerBox = Box.createVerticalBox();
      layerBox.setBorder(BorderFactory.createTitledBorder("Layering"));
      
      JRadioButton noneLayerButton = new JRadioButton("None");
      if (VariableForces.layering == VariableForces.none)
      {
      	noneLayerButton.setSelected(true);
      }
      noneLayerButton.setActionCommand(new String("noneLayer"));
      
      JRadioButton yearLayerButton = new JRadioButton("Year");
      if (VariableForces.layering == VariableForces.year)
      {
      	yearLayerButton.setSelected(true);
      }
      yearLayerButton.setActionCommand(new String("yearLayer"));
      
      JRadioButton degreeLayerButton = new JRadioButton("Degree");
      if (VariableForces.layering == VariableForces.degree)
      {
      	degreeLayerButton.setSelected(true);
      }
      degreeLayerButton.setActionCommand(new String("degreeLayer"));
      
      JRadioButton sphereYearLayerButton = new JRadioButton("Sphere Year");
      if (VariableForces.layering == VariableForces.sphereYear)
      {
      	sphereYearLayerButton.setSelected(true);
      }
      sphereYearLayerButton.setActionCommand(new String("sphereYearLayer"));
      
      JRadioButton sphereDegreeLayerButton = new JRadioButton("Sphere Degree");
      if (VariableForces.layering == VariableForces.sphereDegree)
      {
      	sphereDegreeLayerButton.setSelected(true);
      }
      sphereDegreeLayerButton.setActionCommand(new String("sphereDegreeLayer"));
      
      ButtonGroup layerGroup = new ButtonGroup();
      layerGroup.add(noneLayerButton);
      layerGroup.add(yearLayerButton);
      layerGroup.add(degreeLayerButton);
      layerGroup.add(sphereYearLayerButton);
      layerGroup.add(sphereDegreeLayerButton);
      
      layerBox.add(noneLayerButton);
      layerBox.add(yearLayerButton);
      layerBox.add(degreeLayerButton);
      layerBox.add(sphereYearLayerButton);
      layerBox.add(sphereDegreeLayerButton);
      
      fadeControlsBox.add(layerBox);
      
      yearLayerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      noneLayerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      degreeLayerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      sphereYearLayerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      sphereDegreeLayerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      Box colourBox = Box.createVerticalBox();
      colourBox.setBorder(BorderFactory.createTitledBorder("Colouring"));
      
      JRadioButton defaultColourButton = new JRadioButton("Default");
      if (VariableForces.colouring == VariableForces.defaultColouring)
      {
      	defaultColourButton.setSelected(true);
      }
      defaultColourButton.setActionCommand(new String("defaultColour"));
      
      JRadioButton yearColourButton = new JRadioButton("Year");
      if (VariableForces.colouring == VariableForces.yearColouring)
      {
      	yearColourButton.setSelected(true);
      }
      yearColourButton.setActionCommand(new String("yearColour"));
      
      JRadioButton degreeColourButton = new JRadioButton("Degree");
      if (VariableForces.colouring == VariableForces.degreeColouring)
      {
      	degreeColourButton.setSelected(true);
      }
      degreeColourButton.setActionCommand(new String("degreeColour"));
      
      ButtonGroup colourGroup = new ButtonGroup();
      colourGroup.add(yearColourButton);
      colourGroup.add(degreeColourButton);
      
      
      colourBox.add(yearColourButton);
      colourBox.add(degreeColourButton);
      
      fadeControlsBox.add(colourBox);
      
      yearColourButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
      degreeColourButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedLayerButton_actionPerformed(e);
        }
    });
      
  }
      
  void changedLayerButton_actionPerformed(ActionEvent e)
  {
  	if (e.getActionCommand().equals(new String("noneLayer")))
  	{
  		VariableForces.layering = VariableForces.none;
  	}
  	if (e.getActionCommand().equals(new String("yearLayer")))
  	{
  		VariableForces.layering = VariableForces.year;
  	}
  	if (e.getActionCommand().equals(new String("degreeLayer")))
  	{
  		VariableForces.layering = VariableForces.degree;
  	}
  	if (e.getActionCommand().equals(new String("sphereYearLayer")))
  	{
  		VariableForces.layering = VariableForces.sphereYear;
  	}
  	if (e.getActionCommand().equals(new String("sphereDegreeLayer")))
  	{
  		VariableForces.layering = VariableForces.sphereDegree;
  	}
  	if (e.getActionCommand().equals(new String("yearColour")))
  	{
  		VariableForces.colouring = VariableForces.yearColouring;
  	}
  	if (e.getActionCommand().equals(new String("degreeColour")))
  	{
  		VariableForces.colouring = VariableForces.degreeColouring;
  	}
  	cluster.unfreeze();
  }
}

