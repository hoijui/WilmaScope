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
package org.wilmascope.graphgen;

import java.awt.Color;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.vecmath.Point3f;

import org.wilmascope.control.GraphControl;

/**
 * Boilerplate class for graph generators. Extend this class to create classes
 * that generate random or example graphs.
 * 
 * @author dwyer
 */
public abstract class GraphGenerator {
  /**
   * You should simply return a string constant that is a unique identifier.
   * This string will appear in the Graph Generator Window list so give it a
   * capital letter and spaces where necessary.
   * 
   * @return unique identifier that will also appear in menus
   */
  public abstract String getName();

  /**
   * This method will do the work of actually creating the graph.
   * 
   * @param gc
   *          the instance of GraphControl inwhich the graph will be generated
   */
  public abstract void generate(GraphControl gc);

  /**
   * the best way to use this is to create a JPanel with controls for setting
   * parameters for your graph generator in your constructor and ensure that
   * this method returns a reference to it.
   * 
   * @return a control panel for setting the parameters for the generated graph
   */
  public abstract JPanel getControls();

  /**
   * Set the glyph style for the generated graph
   * 
   * @param nodeView
   *          node view style
   * @param edgeView
   *          edge view style
   */
  public void setView(String nodeView, String edgeView) {
    this.nodeView = nodeView;
    this.edgeView = edgeView;
  }

  /**
   * Create a randomly coloured, randomly positioned node with the default style
   * @param root cluster to add the node to
   * @param threeD use 3D positions
   * @return the new node
   */
  protected GraphControl.NodeFacade addRandomNode(GraphControl.ClusterFacade root, boolean threeD) {
    GraphControl.NodeFacade n = root.addNode(getNodeView());
    n.setColour(GraphGenerator.randomColour());
    n.setPosition(GraphGenerator.randomPoint(threeD));
    return n;
  }
  /**
   * Get the NodeView style 
   * @return
   */
  protected String getNodeView() {
    return nodeView;
  }

  protected String getEdgeView() {
    return edgeView;
  }

  /**
   * Creates a labelled slider suitable for adding to the control panel of
   * graphGenerators
   * 
   * @param label
   *          displayed in border round slider
   * @param min
   *          minimum value of slider
   * @param max
   *          maximim value of slider
   * @param value
   *          default
   * @return the new slider
   */
  protected static JSlider createStandardSlider(String label, int min, int max,
      int value) {
    JSlider slider = new JSlider(min, max, value);
    int range = max - min;
    slider.setBorder(new javax.swing.border.TitledBorder(label));
    slider.setMinorTickSpacing(range / 4);
    slider.setMajorTickSpacing(range / 8);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    slider.setValue(value);
    slider.setLabelTable(slider.createStandardLabels(range / 4));
    return slider;
  }

  /**
   * @return Returns the random generator so it can be reused by other
   *         generators.
   */
  protected static Random getRandom() {
    return GraphGenerator.rand;
  }

  /**
   * @param threeD
   *          if this is false then z values are all 0
   * @return a random point with x,y,z values between 0 and 1
   */
  protected static Point3f randomPoint(boolean threeD) {
    return new Point3f((GraphGenerator.rand.nextFloat() - 0.5f) * 5f,
        (GraphGenerator.rand.nextFloat() - 0.5f) * 5f,
        threeD ? (GraphGenerator.rand.nextFloat() - 0.5f) * 5f : 0f);
  }

  /**
   * @return a random colour
   */
  protected static Color randomColour() {
    return new Color(GraphGenerator.rand.nextFloat(), GraphGenerator.rand
        .nextFloat(), GraphGenerator.rand.nextFloat());
  }

  private String nodeView, edgeView;
  private static Random rand = new Random();
}
