/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope organisation
 * (www.wilmascope.org) however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform us (tgdwyer@yahoo.com)
 * if you do anything really cool with it.
 *
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma
 *
 * -- Tim Dwyer, 2001
 */

package org.wilmascope.forcelayout;

/*
 * ForceControlPanel.java
 *
 * Created on 18 September 2000, 21:57
 */

import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import org.wilmascope.control.GraphControl;
import org.wilmascope.control.WilmaMain;
import org.wilmascope.forcelayout.ForceManager.UnknownForceTypeException;
/**
 *
 * @author  administrator
 * @version
 */
public class ForceControlPanel extends javax.swing.JPanel {
  private Force force;
  private GraphControl.ClusterFacade cluster;
  private ForceLayout forceLayout;
  JCheckBox enabledCheckBox = new JCheckBox();
  JSlider forceSlider = new JSlider();
  TitledBorder titledBorder1;

  public ForceControlPanel(GraphControl.ClusterFacade cluster, Force force) {
    this.cluster = cluster;
    this.force = force;
    this.forceLayout = (ForceLayout)cluster.getLayoutEngine();
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153),2),force.getTypeName());
    this.setBorder(titledBorder1);
    this.add(enabledCheckBox, null);
    forceSlider.setMinorTickSpacing (10);
    forceSlider.setPaintTicks (true);
    forceSlider.setMajorTickSpacing (50);
    forceSlider.setValue ((int)((float)force.getStrengthConstant() * 10f));
    forceSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        forceSliderStateChanged();
      }
    }
    );
    this.add(forceSlider, null);
    // The following nasty bit of code checks to see if we already have this
    // force instantiated in our forceLayout.  If we do then it makes the
    // slider point to this existing one, else it creates a new one.
    Force existingForce;
    if((existingForce = forceLayout.getForce(force.getTypeName()))!=null) {
      enabledCheckBox.setSelected(true);
      force = existingForce;
    } else {
      try {
        force = ForceManager.getInstance().createForce(force.getTypeName());
      } catch (UnknownForceTypeException e1) {
        WilmaMain.showErrorDialog("Unknown Force Type!",e1);
      }
      forceSlider.setEnabled(false);
    }
    enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        enabledCheckBox_actionPerformed(e);
      }
    });
  }
  void forceSliderStateChanged() {
    force.setStrengthConstant(forceSlider.getValue()/10f);
    cluster.unfreeze();
  }
  void enabledCheckBox_actionPerformed(ActionEvent e) {
    if(forceSlider.isEnabled()) {
      forceSlider.setEnabled(false);
      forceLayout.removeForce(force);
    } else {
      forceSlider.setEnabled(true);
      forceLayout.addForce(force);
    }
    cluster.unfreeze();
  }
}
