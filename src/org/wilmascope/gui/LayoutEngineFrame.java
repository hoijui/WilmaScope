package org.wilmascope.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wilmascope.control.GraphControl;
import org.wilmascope.control.WilmaMain;
import org.wilmascope.graph.BalancedEventListener;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.layoutregistry.LayoutManager;
import org.wilmascope.layoutregistry.LayoutManager.UnknownLayoutTypeException;

/**
 * @author dwyer
 *
 * A frame with a panel for selecting the layout engine, and a panel
 * for controls specific to that engine
 */
public class LayoutEngineFrame extends JFrame implements BalancedEventListener {
  private Box box1;
  private JPanel generalControls = new JPanel();
  private JComboBox layoutEngineComboBox;
  private SpinnerSlider animationGranularitySlider = new SpinnerSlider("Iterations per Frame",0,100,1);
  private JButton startStopButton = new JButton();
  private JPanel controlsPanel;
  GraphControl.Cluster cluster;
  public LayoutEngineFrame(GraphControl.Cluster cluster, String title) {
    this.cluster = cluster;
    setTitle (title);
    cluster.getCluster().addBalancedEventListener(this);
    LayoutEngine layoutEngine = cluster.getLayoutEngine();
    layoutEngineComboBox = new JComboBox(LayoutManager.getInstance()
        .getTypeList());
    controlsPanel=layoutEngine.getControls();
    setStartStopButtonLabel();
    startStopButton.addActionListener(new java.awt.event.ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			  startStopButton_actionPerformed(e);
		  }
	  });
    ImageIcon icon = new ImageIcon(org.wilmascope.images.Images.class.getResource("forces.png"));
    this.setIconImage(icon.getImage());
    box1 = Box.createVerticalBox();
    layoutEngineComboBox.setSelectedItem(layoutEngine.getName());
    layoutEngineComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutEngineComboBox_actionPerformed(e);
      }
    });
    this.getContentPane().add(box1, BorderLayout.CENTER);
    box1.add(generalControls, null);
    box1.add(controlsPanel);
    generalControls.setLayout(new BoxLayout(generalControls,BoxLayout.PAGE_AXIS));
    generalControls.add(layoutEngineComboBox, null);
    generalControls.add(startStopButton);
    pack();
  }
  public LayoutEngineFrame(final GraphControl graphControl, String title) {
    this(graphControl.getRootCluster(),title);
    animationGranularitySlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        graphControl.setIterationsPerFrame(animationGranularitySlider.getValue());
      }
    });
    generalControls.add(animationGranularitySlider);
    pack();
  }
  void startStopButton_actionPerformed(ActionEvent e) {
    if(startStopButton.getText().equals("Start")) {
      cluster.unfreeze();
    } else {
      cluster.freeze();
    }
  }
  void setStartStopButtonLabel() {
    if(cluster.getCluster().isBalanced()) {
      startStopButton.setText("Start");
    } else {
      startStopButton.setText("Stop");
    }
  }
  void layoutEngineComboBox_actionPerformed(ActionEvent e) {
    String s = (String)layoutEngineComboBox.getSelectedItem();
    box1.remove(controlsPanel);
    cluster.freeze();
    Cluster c = cluster.getCluster();
    LayoutEngine layoutEngine=null;
    try {
      layoutEngine = LayoutManager.getInstance().createLayout(s);
    } catch (UnknownLayoutTypeException e1) {

      WilmaMain.showErrorDialog("Unknown Layout Type",e1);
    }
    c.setLayoutEngine(layoutEngine);
    controlsPanel = layoutEngine.getControls();
    box1.add(controlsPanel);
    pack();
  }
  public void clusterBalanced(Cluster c, boolean balanced) {
    setStartStopButtonLabel();
  }
}
