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
package org.wilmascope.control;

import java.awt.BorderLayout;
import com.sun.j3d.utils.applet.JMainFrame;
import org.wilmascope.view.PickingClient;
import javax.swing.*;
import org.wilmascope.gui.*;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */
public class WilmaApplet extends JApplet {
  {
    // JPopupMenu won't work over a (heavyweight) Java3D canvas unless
    // we do the following
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
  }
  public void init() {
    getContentPane().setLayout(new BorderLayout());
    graphControl = new GraphControl(600,600);
    java.awt.Component graphCanvas = graphControl.getGraphCanvas();
    getContentPane().add("Center",graphCanvas);
    ControlPanel controlPanel = new ControlPanel();
    getContentPane().add("South",controlPanel);
    //RootClusterMenu rootMenu = new RootClusterMenu(this, r, controlPanel);
    Actions actions = Actions.getInstance();
    actions.init(this,graphControl,controlPanel);
    getContentPane().add("North",actions.getToolPanel());

    MenuBar menuBar = new MenuBar(actions, graphControl, controlPanel);
    setJMenuBar(menuBar);
    //graphControl.setRootPickingClient(rootMenu);
    GraphControl.ClusterFacade r = graphControl.getRootCluster();
    graphControl.getPickListener().setNodeOptionsClient(new NodeOptionsMenu(graphCanvas, graphControl, r, controlPanel));
    graphControl.getPickListener().setClusterOptionsClient(new ClusterOptionsMenu(graphCanvas, r, controlPanel));
    graphControl.getPickListener().setEdgeOptionsClient(new EdgeOptionsMenu(graphCanvas, r));
    try {
      r.addForce("Repulsion").setStrength(1f);
      r.addForce("Spring").setStrength(5f);
      r.addForce("Origin").setStrength(1f);
    } catch(Exception e) {
      System.out.println("Couldn't add forces to graph root while initing applet, reason: "+e.getMessage());
    }
    //r.setBalancedThreshold(0);
    /*
    GraphControl.ClusterFacade c1 = r.addCluster();
    c1.addForce("Repulsion");
    c1.addForce("Spring");
    c1.addForce("Origin").setStrengthConstant(10f);
    c1.setIterations(1);
    GraphControl.ClusterFacade c2 = r.addCluster();
    c2.addForce("Repulsion");
    c2.addForce("Spring");
    c2.addForce("Origin").setStrengthConstant(10f);
    c2.setIterations(1);
    GraphControl.NodeFacade a = c1.addNode();
    GraphControl.NodeFacade b = c1.addNode();
    GraphControl.EdgeFacade e1 = c1.addEdge(a,b);
    GraphControl.NodeFacade c = c2.addNode();
    GraphControl.NodeFacade d = c2.addNode();
    GraphControl.EdgeFacade e2 = c2.addEdge(c,d);
    GraphControl.EdgeFacade e3 = r.addEdge(b,c);

    GraphControl.ClusterFacade c3 = c2.addCluster();
    c3.addForce("Repulsion");
    c3.addForce("Spring");
    c3.addForce("Origin").setStrengthConstant(10f);
    c3.setIterations(1);
    GraphControl.NodeFacade e = c3.addNode();
    GraphControl.NodeFacade f = c3.addNode();
    */
    r.unfreeze();
    //GraphControl.EdgeFacade e4 = c3.addEdge(e,f);
    //GraphControl.EdgeFacade e5 = c2.addEdge(e,d);
  }

  public static void main(String argv[])
  {
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    WilmaApplet main = new WilmaApplet();
    main.createJMainFrame();
  }
  public void createJMainFrame() {
    JMainFrame mf = new JMainFrame(this, 600, 600);
    ImageIcon icon = new ImageIcon(WilmaApplet.class.getResource("/images/WilmaW24.png"));
    mf.setIconImage(icon.getImage());
    mf.setTitle("Welcome to Wilma!");
  }
  GraphControl graphControl;
  public GraphControl getGraphControl() {
    return graphControl;
  }
  public void setSize(int width, int height) {
    //System.out.println("resizing: width="+width+", height="+height);
    super.setSize(width,height);
    getRootPane().updateUI();
    validate();
  }
}
