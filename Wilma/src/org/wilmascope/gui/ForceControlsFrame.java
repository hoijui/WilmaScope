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

package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.Vector;
import org.wilmascope.forcelayout.*;
import org.wilmascope.control        .GraphControl;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

public class ForceControlsFrame extends JFrame {

  public ForceControlsFrame() {
  }
  ForceLayout forceLayout;
  GraphControl.ClusterFacade cluster;
  String title;

  // Variables declaration
  JSlider velocityAttenuationSlider = new JSlider();
  JSlider angularInertiaSlider = new JSlider();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();

  Box boxLayout;
  Box forceLayoutControlsBox;
  Box forceControlsBox;
  JPanel jPanel1 = new JPanel();
  JSlider balancedThresholdSlider = new JSlider();
  JSlider iterationsPerFrameSlider = new JSlider();
  Border border1;
  TitledBorder titledBorder1;

  public ForceControlsFrame(GraphControl.ClusterFacade cluster,String title) {
    try {
      this.title = title;
      this.cluster = cluster;
      this.forceLayout = (ForceLayout)cluster.getLayoutEngine();
      ImageIcon icon = new ImageIcon(getClass().getResource("/images/forces.png"));
      this.setIconImage(icon.getImage());
      jbInit();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(new Color(153, 153, 153),2);
    titledBorder1 = new TitledBorder(border1,"Iterations Per Frame");
    setTitle (title);
    boxLayout = Box.createVerticalBox();
    forceControlsBox = Box.createVerticalBox();
    forceLayoutControlsBox = Box.createVerticalBox();
    this.getContentPane().add(boxLayout, BorderLayout.CENTER);
    boxLayout.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(forceControlsBox, null);
    forceControlsBox.add(jPanel1, null);
    boxLayout.add(jScrollPane2, null);
    jScrollPane2.getViewport().add(forceLayoutControlsBox, null);
    velocityAttenuationSlider.setBorder (new javax.swing.border.TitledBorder("Velocity Scale"));
    velocityAttenuationSlider.setMinorTickSpacing (10);
    velocityAttenuationSlider.setPaintLabels (true);
    velocityAttenuationSlider.setPaintTicks (true);
    velocityAttenuationSlider.setMajorTickSpacing (50);
    velocityAttenuationSlider.setValue ((int)((float)forceLayout.getVelocityAttenuation() * 1000f));
    velocityAttenuationSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        forceLayout.setVelocityAttenuation((float)velocityAttenuationSlider.getValue()/1000f);
      }
    });
    forceLayoutControlsBox.add(velocityAttenuationSlider);
    /*
    angularInertiaSlider.setBorder (new javax.swing.border.TitledBorder("Angular Inertia"));
    angularInertiaSlider.setMinorTickSpacing (10);
    angularInertiaSlider.setPaintLabels (true);
    angularInertiaSlider.setPaintTicks (true);
    angularInertiaSlider.setMajorTickSpacing (50);
    angularInertiaSlider.setValue ((int)((float)forceLayout.getAngularInertia())-1);
    angularInertiaSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        forceLayout.setAngularInertia((float)angularInertiaSlider.getValue()+1);
      }
    });
    forceLayoutControlsBox.add(angularInertiaSlider);
*/
    balancedThresholdSlider.setBorder (new javax.swing.border.TitledBorder("Balanced Threshold"));
    balancedThresholdSlider.setMinorTickSpacing (10);
    balancedThresholdSlider.setPaintLabels (true);
    balancedThresholdSlider.setPaintTicks (true);
    balancedThresholdSlider.setMajorTickSpacing (50);
    balancedThresholdSlider.setValue ((int)((float)forceLayout.getBalancedThreshold() * 1000f));
    balancedThresholdSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        forceLayout.setBalancedThreshold((float)balancedThresholdSlider.getValue()/1000f);
      }
    });
    forceLayoutControlsBox.add(balancedThresholdSlider);

    iterationsPerFrameSlider.setBorder (titledBorder1);
    iterationsPerFrameSlider.setMinorTickSpacing (10);
    iterationsPerFrameSlider.setPaintLabels (true);
    iterationsPerFrameSlider.setPaintTicks (true);
    iterationsPerFrameSlider.setMajorTickSpacing (50);
    iterationsPerFrameSlider.setValue (forceLayout.getIterations());
    iterationsPerFrameSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        forceLayout.setIterations(iterationsPerFrameSlider.getValue());
      }
    });
    forceLayoutControlsBox.add(iterationsPerFrameSlider);
    java.util.Vector forces = new Vector(ForceManager.getInstance().getAvailableForces());
    for(int i=0; i<forces.size(); i++) {
      forceControlsBox.add(new ForceControlPanel(cluster,(Force)forces.elementAt(i)));
    }
  }
}
