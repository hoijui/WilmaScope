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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JPanel;
import javax.vecmath.Vector3f;

import org.wilmascope.control.GraphControl;
import org.wilmascope.control.WilmaMain;
import org.wilmascope.forcelayout.ForceManager.UnknownForceTypeException;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;

/**
 * Main class force for calculating forces on all nodes and moving them
 * incrementally.
 */

public class ForceLayout implements LayoutEngine<NodeForceLayout,EdgeForceLayout> {

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.layoutregistry.LayoutPrototype#create()
   */
  public LayoutEngine create() {
    return new ForceLayout();
  }

  public void init(Cluster root) {
    this.root = root;
    createElementLayouts();
  }

  public void calculateLayout() {
    // Calculate the force on each node for each of our forces
    for (int j = 0; j < forces.size(); j++) {
      Force f = (Force) forces.get(j);
      f.calculate();
    }
  }

  float cool = 1f;

  public void reset() {
    cool = 1f;
  }

  public boolean applyLayout() {
    NodeForceLayout nodeLayout;
    // reposition nodes, calculating maxNetForce as you go
    float maxNetForce = 0;
    float currentNetForce = 0;
    NodeList nodes = root.getNodes();
    EdgeList edges = root.getInternalEdges();
    for (Node n : nodes) {
      nodeLayout = (NodeForceLayout) (n.getLayout());
      nodeLayout.getNetForce().scale(cool);
      //cool*=0.99;
      currentNetForce = nodeLayout.getNetForce().length();
      if (currentNetForce > maxNetForce) {
        maxNetForce = currentNetForce;
      }
      nodeLayout.applyForce(velocityAttenuation);
      if (constrained) {
        nodeLayout.applyConstraint();
      }
    }
    for (int i = 0; i < edges.size(); i++) {
      edges.get(i).recalculate();
    }
    if (maxNetForce < balancedThreshold) {
      if (balancedEventClient != null)
        balancedEventClient.callback();
      reset();
      return true;
    }
    return false;
  }

  /**
   * Add a force to ForceLayout's list of forces to apply
   */
  public void addForce(Force force) {
    forces.add(force);
    force.setCluster(root);
  }

  /**
   * Remove a force from ForceLayout's list of forces to apply
   */
  public void removeForce(Force force) {
    forces.remove(force);
  }

  public void removeAllForces() {
    forces.clear();
  }

  public Vector getForces() {
    return forces;
  }

  /**
   * Get a reference to one of our forces by name
   */
  public Force getForce(String name) {
    for (int i = 0; i < forces.size(); i++) {
      Force force = (Force) forces.get(i);
      if (name.equals(force.getTypeName()))
        return force;
    }
    return null;
  }

  public void setBalancedEventClient(BalancedEventClient c) {
    balancedEventClient = c;
  }

  public void setBalancedThreshold(float threshold) {
    this.balancedThreshold = threshold;
  }

  public float getBalancedThreshold() {
    return balancedThreshold;
  }

  public float getVelocityAttenuation() {
    return velocityAttenuation;
  }

  public void setVelocityAttenuation(float va) {
    velocityAttenuation = va;
    System.out.println("VA=" + va);
  }

  public void setConstrained() {
    constrained = true;
  }

  public void createElementLayouts() {
    NodeList nodes = root.getNodes();
    for (Node n : nodes) {
      n.setLayout(createNodeLayout(n));
    }
    EdgeList edges = root.getInternalEdges();
    for (Edge e : edges) {
      e.setLayout(createEdgeLayout(e));
    }
  }

  public NodeForceLayout createNodeLayout(
      org.wilmascope.graph.Node n) {
    return new NodeForceLayout();
  }

  public EdgeForceLayout createEdgeLayout(
      org.wilmascope.graph.Edge e) {
    return new EdgeForceLayout();
  }

  public void setFrictionCoefficient(float friction) {
    Constants.frictionCoefficient = friction;
  }

  // an array of the forces to apply
  Vector forces = new Vector();

  float velocityAttenuation = Constants.velocityAttenuation;

  Cluster root;

  Vector3f vY = new Vector3f(0, 1f, 0);

  boolean constrained = false;

  private float balancedThreshold = 0.01f;

  private BalancedEventClient balancedEventClient = null;

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.graph.LayoutEngine#getControls()
   */
  public JPanel getControls() {
    return new ForceControlsPanel((GraphControl.Cluster) root.getUserFacade());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.graph.LayoutEngine#getProperties()
   */
  public Properties getProperties() {
    Properties p = new Properties();
    for (Iterator i = forces.iterator(); i.hasNext();) {
      Force f = (Force) i.next();
      p.setProperty(f.getTypeName(), "" + f.getStrengthConstant());
    }
    p.setProperty("VelocityAttenuation", "" + getVelocityAttenuation());
    return p;
  }

  public String getName() {
    return "Force Directed";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wilmascope.graph.LayoutEngine#setProperties(java.util.Properties)
   */
  public void setProperties(Properties p) {
    ForceManager m = ForceManager.getInstance();
    for (Enumeration k = p.keys(); k.hasMoreElements();) {
      String forceName = (String) k.nextElement();
      float strength = Float.parseFloat(p.getProperty(forceName));
      if (forceName.equals("VelocityAttenuation")) {
        setVelocityAttenuation(strength);
      } else {
        try {
          Force f = m.createForce(forceName);
          f.setStrengthConstant(strength);
          addForce(f);
        } catch (ForceManager.UnknownForceTypeException e) {
          WilmaMain.showErrorDialog("Unknown Force Type", e);
        }
      }
    }
  }

  public static ForceLayout createDefaultForceLayout(Cluster root) {
    ForceLayout fl = new ForceLayout();
    fl.init(root);
    try {
      fl.addForce(ForceManager.getInstance().createForce("Spring"));
      fl.addForce(ForceManager.getInstance().createForce("Repulsion"));
      fl.addForce(ForceManager.getInstance().createForce("Origin"));
      fl.getForce("Origin").setStrengthConstant(2f);
    } catch (UnknownForceTypeException e) {
      WilmaMain.showErrorDialog("Problems creating default ForceLayout", e);
    }
    return fl;
  }

  public static ForceLayout createDefaultClusterForceLayout(Cluster root) {
    ForceLayout fl = createDefaultForceLayout(root);
    fl.getForce("Origin").setStrengthConstant(10f);
    return fl;
  }
}
