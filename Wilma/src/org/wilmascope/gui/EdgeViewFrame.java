/*
 * EdgeViewFrame.java
 *
 * Created on December 18, 2001, 5:11 PM
 */

package org.wilmascope.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.wilmascope.control.GraphControl;
import org.wilmascope.view.EdgeView;
/**
 *
 * @author  dwyer
 */
class ColourPanel extends JPanel {
  public ColourPanel(int leftInset, int rightInset) {
    this.leftInset = leftInset;
    this.rightInset = rightInset;
  }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Insets insets = getInsets();
    int width = getWidth() - insets.left - insets.right - leftInset - rightInset;
    int height = getHeight() - insets.top - insets.bottom;
    //Paint a rectangle on top of the image.
    for(int i=0; i<width; i++) {
      float hue = 1f-(float)i/(float)width;
      g.setColor(Color.getHSBColor(hue,1f,1f));
      g.drawLine(leftInset+i,0,leftInset+i,height);
    }
  }
  int leftInset, rightInset;
}

public class EdgeViewFrame extends JFrame {

  /** Creates new form EdgeViewFrame */
  public EdgeViewFrame(GraphControl gc, GraphControl.ClusterFacade root) {
    this.rootCluster = root;
    this.graphControl = gc;
    initComponents();
    minLabelPanel.setBackground(getColourFromSlider(minHueSlider));
    maxLabelPanel.setBackground(getColourFromSlider(maxHueSlider));
  }

  /** This method is called from within the constructor to
   * initialize the form.
   */
  private void initComponents() {
    Box optionsBox = Box.createVerticalBox();
    Box hideBox = Box.createHorizontalBox();
    Box hueBox = Box.createHorizontalBox();
    Box hueSettingsBox = Box.createVerticalBox();

    hideApplyButton = new JButton();
    thresholdLabel = new JLabel();
    hueLabel = new JLabel();
    hideThresholdSlider = new JSlider();
    hueApplyButton = new JButton();
    hueSliderPanel = new ColourPanel(7,7);
    minLabel = new JLabel();
    minHueSlider = new JSlider();
    maxLabel = new JLabel();
    maxHueSlider = new JSlider();
    minLabelPanel = new JPanel();
    maxLabelPanel = new JPanel();
    closePanel = new JPanel();
    closeButton = new JButton();

    thresholdLabel.setText("Hide by weight threshold:");
    hideBox.add(thresholdLabel);

    hideThresholdSlider.setMinorTickSpacing(10);
    hideThresholdSlider.setPaintTicks(true);
    hideThresholdSlider.setMajorTickSpacing(20);
    hideBox.add(hideThresholdSlider);

    hideApplyButton.setText("Apply");
    hideApplyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        hideApplyButtonActionPerformed(evt);
      }
    });
    hideBox.add(Box.createHorizontalStrut(10));
    hideBox.add(hideApplyButton);

    optionsBox.add(hideBox);

    hueLabel.setText("Show weight by hue:");
    hueBox.add(hueLabel);
    hueSliderPanel.setLayout(new BoxLayout(hueSliderPanel, BoxLayout.Y_AXIS));

    minLabel.setText("Min");
    minLabel.setHorizontalAlignment(SwingConstants.TRAILING);
    minLabelPanel.setLayout(new BorderLayout());
    minLabelPanel.add(minLabel,BorderLayout.WEST);
    hueSettingsBox.add(minLabelPanel);

    minHueSlider.setValue(30);
    minHueSlider.setPaintTrack(false);
    minHueSlider.setOpaque(false);
    minHueSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        minHueSliderStateChanged(evt);
      }
    });

    hueSliderPanel.add(minHueSlider);

    maxLabel.setText("Max");
    maxLabel.setHorizontalAlignment(SwingConstants.TRAILING);
    maxLabelPanel.setLayout(new BorderLayout());
    maxLabelPanel.add(maxLabel,BorderLayout.EAST);

    maxHueSlider.setValue(100);
    maxHueSlider.setOpaque(false);
    maxHueSlider.setPaintTrack(false);
    maxHueSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        maxHueSliderStateChanged(evt);
      }
    });

    hueSliderPanel.add(maxHueSlider);

    hueSettingsBox.add(hueSliderPanel);

    hueSettingsBox.add(maxLabelPanel);

    hueBox.add(hueSettingsBox);
    hueApplyButton.setText("Apply");
    hueApplyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        hueApplyButtonActionPerformed(evt);
      }
    });

    hueBox.add(Box.createHorizontalStrut(10));
    hueBox.add(hueApplyButton);

    optionsBox.add(hueBox);

    getContentPane().add(optionsBox, java.awt.BorderLayout.CENTER);

    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        closeButtonActionPerformed(evt);
      }
    });

    closePanel.add(closeButton);

    getContentPane().add(closePanel, java.awt.BorderLayout.SOUTH);

    pack();
  }

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
    dispose();
  }

  private void hueApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    GraphControl.EdgeFacade edges[] = rootCluster.getEdges();
    for(int i=0;i<edges.length;i++) {
      EdgeView v = (EdgeView)edges[i].getView();
      if(v!=null) {
        v.setHueByWeight(getHueFromSlider(minHueSlider),
          getHueFromSlider(maxHueSlider));
      }
    }
  }
  private void hideApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    GraphControl.EdgeFacade edges[] = rootCluster.getEdges();
    float threshold = (float)(hideThresholdSlider.getValue()) / 100f;
    System.out.println("Threshold = "+ threshold);
    for(int i=0;i<edges.length;i++) {
      EdgeView v = (EdgeView)edges[i].getView();
      if(edges[i].getWeight() < threshold) {
        v.hide();
      } else {
        v.show(graphControl.getGraphCanvas());
      }
    }
  }

  private void maxHueSliderStateChanged(ChangeEvent evt) {
    maxLabelPanel.setBackground(getColourFromSlider(maxHueSlider));
  }

  private void minHueSliderStateChanged(ChangeEvent evt) {
    minLabelPanel.setBackground(getColourFromSlider(minHueSlider));
  }

  private Color getColourFromSlider(JSlider s) {
    return Color.getHSBColor(getHueFromSlider(s),1f,1f);
  }
  private float getHueFromSlider(JSlider s) {
    return (float)(100-s.getValue())/100f;
  }

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    dispose();
  }

  private JSlider hideThresholdSlider;
  private ColourPanel hueSliderPanel;
  private JLabel maxLabel;
  private JSlider minHueSlider;
  private JButton closeButton;
  private JLabel minLabel;
  private JButton hideApplyButton;
  private JSlider maxHueSlider;
  private JButton hueApplyButton;
  private JPanel closePanel;
  private JLabel thresholdLabel;
  private JLabel hueLabel;
  private JPanel minLabelPanel;
  private JPanel maxLabelPanel;

  private GraphControl.ClusterFacade rootCluster;
  private GraphControl graphControl;
}


