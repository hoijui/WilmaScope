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



public class FadeEdgeControlPanel extends javax.swing.JPanel {
  //private String forceName;
  private GraphControl.ClusterFacade cluster;
  private FadeLayout fadeLayout;
  JCheckBox enabledCheckBox = new JCheckBox();
  JSlider forceSlider = new JSlider();
  TitledBorder titledBorder1;
  JCheckBox repelCheckBox = new JCheckBox("Repelling Edges");
  JCheckBox degreeCheckBox = new JCheckBox("In-Degree Only");
  JCheckBox weightCheckBox = new JCheckBox("Weights");

  public FadeEdgeControlPanel(GraphControl.ClusterFacade cluster) {
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
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153),2), new String ("Edge Force"));
    this.setBorder(titledBorder1);
    this.add(enabledCheckBox, null);
    forceSlider.setMinorTickSpacing (10);
    forceSlider.setPaintTicks (true);
    forceSlider.setMajorTickSpacing (50);
    forceSlider.setBorder(BorderFactory.createTitledBorder("Edge Resilience"));
    forceSlider.setValue ((int)(VariableForces.edgeResilience * 100.0f * 2.0f));
    forceSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        VariableForces.edgeResilience = ((float)forceSlider.getValue())/100.0f/2.0f;
        cluster.unfreeze();
      }
    }
    );
    this.add(forceSlider, null);
    
    
    
    
    //Box repelBox = Box.createVerticalBox();
    //repelBox.setBorder(BorderFactory.createTitledBorder("Repelling Edges"));
      
    this.add(repelCheckBox);
    this.add(degreeCheckBox);
    this.add(weightCheckBox);
    
    //this.add(repelBox);
    
    
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
    if (VariableForces.edgeForce)
    {
    	enabledCheckBox.setSelected(true);
    }
    enabledCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedCheckBox_actionPerformed(e);
        }
    });
    
    if (VariableForces.repellingEdges)
    {
    	repelCheckBox.setSelected(true);
    }
    repelCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedRepelCheckBox_actionPerformed(e);
        }
    });
    
    if (VariableForces.inOnly)
    {
    	degreeCheckBox.setSelected(true);
    }
    degreeCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedDegreeCheckBox_actionPerformed(e);
        }
    });
    
    if (VariableForces.edgeWeights)
    {
    	weightCheckBox.setSelected(true);
    }
    weightCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedWeightCheckBox_actionPerformed(e);
        }
    });
  }

  void changedCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      //forceSlider.setEnabled(false);
      //fadeLayout.removeEdgeForce();
      VariableForces.edgeForce = false;
    } else {
      //forceSlider.setEnabled(true);
      //fadeLayout.addEdgeForce();
      VariableForces.edgeForce = true;
    }
    cluster.unfreeze();
  }
  
  void changedRepelCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      //forceSlider.setEnabled(false);
      //fadeLayout.removeEdgeForce();
      VariableForces.repellingEdges = false;
    } else {
      //forceSlider.setEnabled(true);
      //fadeLayout.addEdgeForce();
      VariableForces.repellingEdges = true;
    }
    cluster.unfreeze();
  }
  
  void changedDegreeCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      VariableForces.inOnly = false;
    } else {
      VariableForces.inOnly = true;
    }
    cluster.unfreeze();
  }
  
  void changedWeightCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      VariableForces.edgeWeights = false;
    } else {
      VariableForces.edgeWeights = true;
    }
    cluster.unfreeze();
  }
}
