/*
 * Created on Jun 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.fadelayout;

/**
 * @author cmurray
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import org.wilmascope.control.GraphControl;

import javax.swing.*;




public class FadeNodeControlPanel extends javax.swing.JPanel {
  //private String forceName;
  private GraphControl.ClusterFacade cluster;
  private FadeLayout fadeLayout;
  JCheckBox enabledCheckBox = new JCheckBox();
  JSlider forceSlider = new JSlider();
  JSlider accuracySlider = new JSlider();
  TitledBorder titledBorder1;
  ButtonGroup group;

  public FadeNodeControlPanel(GraphControl.ClusterFacade cluster) {
    this.cluster = cluster;
    //this.forceName = forceName;
    this.fadeLayout = (FadeLayout)cluster.getLayoutEngine();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153),2), new String ("Node Force"));
    this.setBorder(titledBorder1);
    this.add(enabledCheckBox, null);
    forceSlider.setMinorTickSpacing (10);
    forceSlider.setPaintTicks (true);
    forceSlider.setMajorTickSpacing (50);
    forceSlider.setBorder(BorderFactory.createTitledBorder("Repulsion Intensity"));
    //forceSlider.setValue ((int)((float)force.getStrengthConstant() * 10f));
    forceSlider.setValue ((int)(VariableForces.repelIntensity * 100.0f));
    forceSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        //force.setStrengthConstant(forceSlider.getValue()/10f);
      	VariableForces.repelIntensity = ((float)forceSlider.getValue())/100.0f;
        cluster.unfreeze();
      }
    }
    );
    this.add(forceSlider, null);
    
    accuracySlider.setMinorTickSpacing (10);
    accuracySlider.setPaintTicks (true);
    accuracySlider.setMajorTickSpacing (50);
    accuracySlider.setBorder(BorderFactory.createTitledBorder("Accuracy Vs Speed"));
    //forceSlider.setValue ((int)((float)force.getStrengthConstant() * 10f));
    accuracySlider.setValue((int)(VariableForces.accuracyParameter/5.0f * 100.0f));
    accuracySlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        //force.setStrengthConstant(forceSlider.getValue()/10f);
      	VariableForces.accuracyParameter = (((float)accuracySlider.getValue())/100.0f)*5.0f;
        cluster.unfreeze();
      }
    }
    );
    this.add(accuracySlider, null);
    
    //enabledCheckBox.setSelected(true);
    if (VariableForces.nonEdgeForce)
    {
    	enabledCheckBox.setSelected(true);
    }
    enabledCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedCheckBox_actionPerformed(e);
        }
    });
    
    
//  Create the radio buttons.
    JRadioButton bhButton = new JRadioButton(new String("Barnes-Hut Method"));
    if (VariableForces.openingCriterion.equals(new String("bh")))
    {
    	bhButton.setSelected(true);
    }
    bhButton.setActionCommand(new String("bh"));

    JRadioButton mindButton = new JRadioButton(new String("Min Distance"));
    if (VariableForces.openingCriterion.equals(new String("mind")))
    {
    	mindButton.setSelected(true);
    }
    mindButton.setActionCommand(new String("mind"));
    
    JRadioButton bMaxButton = new JRadioButton(new String("bMax"));
    if (VariableForces.openingCriterion.equals(new String("bMax")))
    {
    	bMaxButton.setSelected(true);
    }
    bMaxButton.setActionCommand(new String("bMax"));
    
    JRadioButton newbhButton = new JRadioButton(new String("New Barnes-Hut"));
    if (VariableForces.openingCriterion.equals(new String("newbh")))
    {
    	newbhButton.setSelected(true);
    }
    newbhButton.setActionCommand(new String("newbh"));
    
    JRadioButton odButton = new JRadioButton(new String("Orthogonal Distance"));
    if (VariableForces.openingCriterion.equals(new String("od")))
    {
    	odButton.setSelected(true);
    }
    odButton.setActionCommand(new String("od"));
    
    //Group the radio buttons.
    group = new ButtonGroup();
    group.add(bhButton);
    group.add(mindButton);
    group.add(bMaxButton);
    group.add(newbhButton);
    group.add(odButton);
    
    Box criterionBox = Box.createVerticalBox();
    criterionBox.setBorder(BorderFactory.createTitledBorder("Cell Opening Criterion"));
      
    criterionBox.add(bhButton);
    //criterionBox.add(mindButton);
    //criterionBox.add(bMaxButton);
    //criterionBox.add(newbhButton);
    //criterionBox.add(odButton);
    
    this.add(criterionBox);
    
    bhButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedButton_actionPerformed(e);
        }
    });
    
    newbhButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedButton_actionPerformed(e);
        }
    });
    
    odButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          changedButton_actionPerformed(e);
        }
    });
    
    // The following nasty bit of code checks to see if we already have this
    // force instantiated in our forceLayout.  If we do then it makes the
    // slider point to this existing one, else it creates a new one.
    /*Force existingForce;
    if((existingForce = forceLayout.getForce(force.getTypeName()))!=null) {
      enabledCheckBox.setSelected(true);
      force = existingForce;
    } else {
      force = ForceManager.getInstance().createForce(force.getTypeName());
      forceSlider.setEnabled(false);
    }
    enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        enabledCheckBox_actionPerformed(e);
      }
    });*/
  }

  void changedCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      //forceSlider.setEnabled(false);
      VariableForces.nonEdgeForce = false;
    } else {
      //forceSlider.setEnabled(true);
      VariableForces.nonEdgeForce = true;
    }
    cluster.unfreeze();
  }
  
  void changedButton_actionPerformed(ActionEvent e)
  {
  	VariableForces.openingCriterion = e.getActionCommand();
  }
}
