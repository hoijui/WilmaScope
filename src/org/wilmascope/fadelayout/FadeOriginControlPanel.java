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



public class FadeOriginControlPanel extends javax.swing.JPanel {
  //private String forceName;
  private GraphControl.Cluster cluster;
  private FadeLayout fadeLayout;
  JCheckBox enabledCheckBox = new JCheckBox();
  JSlider forceSlider = new JSlider();
  TitledBorder titledBorder1;

  public FadeOriginControlPanel(final GraphControl.Cluster cluster) {
    this.cluster = cluster;
    //this.forceName = forceName;
    this.fadeLayout = (FadeLayout)cluster.getLayoutEngine();
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153),2), new String ("Origin Force"));
    this.setBorder(titledBorder1);
    this.add(enabledCheckBox, null);
    forceSlider.setMinorTickSpacing (10);
    forceSlider.setPaintTicks (true);
    forceSlider.setMajorTickSpacing (50);
    forceSlider.setBorder(BorderFactory.createTitledBorder("Attraction Intensity"));
    forceSlider.setValue ((int)(VariableForces.originIntensity * 100.0f * 2.0f));
    forceSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        VariableForces.originIntensity = ((float)forceSlider.getValue())/100.0f/2.0f;
        cluster.unfreeze();
      }
    }
    );
    this.add(forceSlider, null);
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
    if (VariableForces.originForce)
    {
    	enabledCheckBox.setSelected(true);
    }
    enabledCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          changedCheckBox_actionPerformed(e);
        }
    });
  }

  void changedCheckBox_actionPerformed(ItemEvent e) {
    if(e.getStateChange() == ItemEvent.DESELECTED) {
      //forceSlider.setEnabled(false);
      //fadeLayout.removeEdgeForce();
      VariableForces.originForce = false;
    } else {
      //forceSlider.setEnabled(true);
      //fadeLayout.addEdgeForce();
      VariableForces.originForce = true;
    }
    cluster.unfreeze();
  }
}
