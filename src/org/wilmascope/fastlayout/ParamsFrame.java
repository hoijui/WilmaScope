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

public class ParamsFrame extends JFrame {

  FastLayout layout;
  String title;

  JSlider attractSlider = new JSlider(1, 20, Defaults.ATTRACTION);
  JSlider repelSlider = new JSlider(1, 20, Defaults.REPULSION);
  JSlider footSlider = new JSlider(1, 20, Defaults.NODE_FOOTPRINT);
//  JSlider minDenseSlider = new JSlider();
  JSlider boilJumpSlider = new JSlider(1, 20, (int)Defaults.BOIL_JUMP);
  JSlider radiusSlider = new JSlider(1, 5000, Defaults.FIELD_RADIUS);
  JSlider resSlider = new JSlider(1, 20, Defaults.FIELD_RES);
  JSlider iterSlider = new JSlider(1, 1000, Defaults.ITERATIONS);
  JSlider simmerSlider = new JSlider(0,100, (int)(Defaults.SIMMER_RATE*100));
  JSlider maxBarrierSlider = new JSlider((int)(Defaults.MIN_BARRIER_RATE*100) ,100, (int)(Defaults.MAX_BARRIER_RATE*100));
  JSlider minBarrierSlider = new JSlider(0, (int)(Defaults.MAX_BARRIER_RATE*100), (int)(Defaults.MIN_BARRIER_RATE*100));
  JSlider boilLenSlider = new JSlider(0, 100, (int)(Defaults.BOIL_LENGTH*100));
  JSlider quenchLenSlider = new JSlider(0, 100-boilLenSlider.getValue(), (int)(Defaults.QUENCH_LENGTH*100));

  JPanel checkPanel = new JPanel();

  JCheckBox centreCheck = new JCheckBox("Enable graph centring", Defaults.CENTRE_FLAG);
  JCheckBox colourCheck = new JCheckBox("Enable potential colour coding", Defaults.COLOUR_FLAG);
  JCheckBox candyCheck = new JCheckBox("Enable ridiculous eye-candy", Defaults.EYE_CANDY_FLAG);

  JButton flattenButton = new JButton("Flatten graph");

  Box box;
  Box boxLayout1;
  Box boxLayout2;

  public ParamsFrame(FastLayout layout, String title) {
    try {
      this.title = title;
      this.layout = layout;
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

    setTitle(title);
    box = Box.createHorizontalBox();
    boxLayout1 = Box.createVerticalBox();
    boxLayout2 = Box.createVerticalBox();
    box.add(boxLayout1);
    box.add(boxLayout2);
    this.getContentPane().add(box, null);

    attractSlider.setBorder (new TitledBorder("Attraction scale factor"));
    attractSlider.setMinorTickSpacing (1);
    attractSlider.setPaintLabels (true);
    attractSlider.setPaintTicks (true);
    attractSlider.setMajorTickSpacing (5);
    attractSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setAttract(attractSlider.getValue());
      }
    });
    boxLayout1.add(attractSlider);

    repelSlider.setBorder (new TitledBorder("Repulsion scale factor"));
    repelSlider.setMinorTickSpacing (1);
    repelSlider.setPaintLabels (true);
    repelSlider.setPaintTicks (true);
    repelSlider.setMajorTickSpacing (5);
    repelSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setRepel(repelSlider.getValue());
      }
    });
    boxLayout1.add(repelSlider);

    boilJumpSlider.setBorder (new TitledBorder("Maximum jump"));
    boilJumpSlider.setMinorTickSpacing (1);
    boilJumpSlider.setPaintLabels (true);
    boilJumpSlider.setPaintTicks (true);
    boilJumpSlider.setMajorTickSpacing (5);
    boilJumpSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setBoilJump(boilJumpSlider.getValue());
      }
    });
    boxLayout1.add(boilJumpSlider);

    footSlider.setBorder (new TitledBorder("Node footprint"));
    footSlider.setMinorTickSpacing (1);
    footSlider.setPaintLabels (true);
    footSlider.setPaintTicks (true);
    footSlider.setMajorTickSpacing (5);
    footSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setFootprint(footSlider.getValue());
      }
    });
    boxLayout1.add(footSlider);

    resSlider.setBorder (new TitledBorder("Field Resolution"));
    resSlider.setMinorTickSpacing (1);
    resSlider.setPaintLabels (true);
    resSlider.setPaintTicks (true);
    resSlider.setMajorTickSpacing (5);
    resSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setRes(resSlider.getValue());
      }
    });
    boxLayout1.add(resSlider);

    radiusSlider.setBorder (new TitledBorder("Field Radius"));
    radiusSlider.setMinorTickSpacing (250);
    radiusSlider.setPaintLabels (true);
    radiusSlider.setPaintTicks (true);
    radiusSlider.setMajorTickSpacing (1250);
    radiusSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setRadius(radiusSlider.getValue());
      }
    });
    boxLayout1.add(radiusSlider);

    simmerSlider.setBorder (new TitledBorder("Simmer Rate (%)"));
    simmerSlider.setMinorTickSpacing (5);
    simmerSlider.setPaintLabels (true);
    simmerSlider.setPaintTicks (true);
    simmerSlider.setMajorTickSpacing (25);
    simmerSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setSimmerRate(simmerSlider.getValue()/100d);
      }
    });
    boxLayout1.add(simmerSlider);

    boilLenSlider.setBorder (new TitledBorder("Time spent in boiling phase (%)"));
    boilLenSlider.setMinorTickSpacing (5);
    boilLenSlider.setPaintLabels (true);
    boilLenSlider.setPaintTicks (true);
    boilLenSlider.setMajorTickSpacing (25);
    boilLenSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        quenchLenSlider.setMaximum(100-boilLenSlider.getValue());
        layout.setBoilLen(boilLenSlider.getValue()/100d);
      }
    });
    boxLayout2.add(boilLenSlider);

    quenchLenSlider.setBorder (new TitledBorder("Time spent in quenching phase (%)"));
    quenchLenSlider.setMinorTickSpacing (5);
    quenchLenSlider.setPaintLabels (true);
    quenchLenSlider.setPaintTicks (true);
    quenchLenSlider.setMajorTickSpacing (25);
    quenchLenSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        boilLenSlider.setMaximum(100-quenchLenSlider.getValue());
        layout.setQuenchLen(quenchLenSlider.getValue()/100d);
      }
    });
    boxLayout2.add(quenchLenSlider);

    maxBarrierSlider.setBorder (new TitledBorder("Max barrier jump rate (%)"));
    maxBarrierSlider.setMinorTickSpacing (5);
    maxBarrierSlider.setPaintLabels (true);
    maxBarrierSlider.setPaintTicks (true);
    maxBarrierSlider.setMajorTickSpacing (25);
    maxBarrierSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        minBarrierSlider.setMaximum(maxBarrierSlider.getValue());
        layout.setMaxBarrier(maxBarrierSlider.getValue()/100d);
      }
    });
    boxLayout2.add(maxBarrierSlider);

    minBarrierSlider.setBorder (new TitledBorder("Min barrier jump rate (%)"));
    minBarrierSlider.setMinorTickSpacing (5);
    minBarrierSlider.setPaintLabels (true);
    minBarrierSlider.setPaintTicks (true);
    minBarrierSlider.setMajorTickSpacing (25);
    minBarrierSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        maxBarrierSlider.setMinimum(minBarrierSlider.getValue());
        layout.setMinBarrier(minBarrierSlider.getValue()/100d);
      }
    });
    boxLayout2.add(minBarrierSlider);

    iterSlider.setBorder (new TitledBorder("Iterations"));
    iterSlider.setMinorTickSpacing (50);
    iterSlider.setPaintLabels (true);
    iterSlider.setPaintTicks (true);
    iterSlider.setMajorTickSpacing (250);
    iterSlider.addChangeListener (new javax.swing.event.ChangeListener () {
      public void stateChanged (javax.swing.event.ChangeEvent evt) {
        layout.setIterations(iterSlider.getValue());
      }
    });
    boxLayout2.add(iterSlider);

    checkPanel.setLayout(new GridLayout(3,1));
    checkPanel.setBorder(new TitledBorder("Algorithm Flags"));

    centreCheck.addItemListener(new ItemListener () {
      public void itemStateChanged (ItemEvent e) {
        layout.setCentreFlag(centreCheck.isSelected());
      }
    });
    checkPanel.add(centreCheck);

    colourCheck.addItemListener(new ItemListener () {
      public void itemStateChanged (ItemEvent e) {
        layout.setColourFlag(colourCheck.isSelected());
      }
    });
    checkPanel.add(colourCheck);

    candyCheck.addItemListener(new ItemListener () {
      public void itemStateChanged (ItemEvent e) {
        layout.setEyeCandyFlag(candyCheck.isSelected());
      }
    });
    checkPanel.add(candyCheck);

    boxLayout2.add(checkPanel);

    flattenButton.addItemListener(new ItemListener() {
      public void itemStateChanged (ItemEvent e) {
        layout.flattenGraph();
      }
    });
    boxLayout2.add(flattenButton);

  }
}