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

/**
 * This class defines some standard actions that may be performed and puts them
 * in toolbars and menus
 */
import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import org.wilmascope.control.GraphControl;
import org.wilmascope.view.ViewManager;
import org.wilmascope.file.FileHandler;
public class Actions {
  ActionMap actionMap = new ActionMap();
  FileHandler fileHandler;
  Action fileNewAction;
  Action addNodeAction;
  Action addEdgeAction;
  Action addClusterAction;
  Action pickableClusterAction;
  Action showHiddenAction;
  Action adjustForcesAction;
  Action graphOperationsAction;
  Action fileOpenAction;
  Action fileSaveAction;
  Action fileSaveAsAction;
  public void setEnabled(boolean enabled){
    addNodeAction.setEnabled(enabled);
    addEdgeAction.setEnabled(enabled);
    addClusterAction.setEnabled(enabled);
    pickableClusterAction.setEnabled(enabled);
    showHiddenAction.setEnabled(enabled);
    fileNewAction.setEnabled(enabled);
  }
  protected Actions() {};
  public void init(final Component parent, GraphControl graphControl, final ControlPanel controlPanel) {
    final GraphControl.ClusterFacade rootCluster = graphControl.getRootCluster();
    fileHandler = new FileHandler(graphControl);
    addNodeAction = new AbstractAction(
      "Add Node", new ImageIcon(getClass().getResource("/images/node.png")))
    {
      public void actionPerformed(ActionEvent e) {
        GraphControl.NodeFacade n = rootCluster.addNode();
        rootCluster.unfreeze();
      }
    };
    addEdgeAction = new AbstractAction("Add Edge",
      new ImageIcon(getClass().getResource("/images/edge.png")))
    {
      public void actionPerformed(ActionEvent e) {
        GraphControl.getPickListener().enableMultiPicking(2,
          new Class[]{GraphControl.nodeClass});
        controlPanel.add(new EdgePanel(controlPanel, rootCluster));
        controlPanel.updateUI();
      }
    };
    addClusterAction = new AbstractAction("Add Cluster",
      new ImageIcon(getClass().getResource("/images/cluster.png")))
    {
      public void actionPerformed(ActionEvent e) {
        controlPanel.add(new ClusterPanel(controlPanel, rootCluster));
        controlPanel.updateUI();
        GraphControl.getPickListener().enableMultiPicking(100,
          new Class[]{GraphControl.nodeClass,GraphControl.clusterClass});
      }
    };
    pickableClusterAction = new AbstractAction("Make all clusters pickable",
      new ImageIcon(getClass().getResource("/images/pickableCluster.png")))
    {
      public void actionPerformed(ActionEvent e) {
        rootCluster.childrenPickable();
      }
    };
    showHiddenAction = new AbstractAction("Make hidden objects visible",
      new ImageIcon(getClass().getResource("/images/find.png")))
    {
      public void actionPerformed(ActionEvent e) {
        rootCluster.showHiddenChildren();
      }
    };
    adjustForcesAction = new AbstractAction("Adjust Forces",
      new ImageIcon(getClass().getResource("/images/forces.png")))
    {
      public void actionPerformed(ActionEvent e) {
        ForceControlsFrame forceControls
          = new ForceControlsFrame(rootCluster,"Global Force Controls");
        forceControls.show();
      }
    };
    graphOperationsAction = new AbstractAction("Graph Operations")
    {
      public void actionPerformed(ActionEvent e) {
        ClusteriseFrame graphOps
          = new ClusteriseFrame(rootCluster,"Graph Operations");
        graphOps.show();
      }
    };
    fileOpenAction = new AbstractAction("Open",
      new ImageIcon(getClass().getResource("/images/Open24.gif")))
    {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(org.wilmascope.global.Constants.getInstance().getProperty("DefaultDataPath"));
        chooser.setFileFilter(fileHandler.getFileFilter());
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          fileHandler.load(chooser.getSelectedFile().getAbsolutePath());
        }
      }
    };
    configAction(fileOpenAction,"Open a previously saved graph.","control O",'O', "/images/Open16.gif");
    fileNewAction = new AbstractAction("New",
      new ImageIcon(getClass().getResource("/images/New24.gif")))
    {
      public void actionPerformed(ActionEvent e) {
        rootCluster.deleteAll();
      }
    };
    configAction(fileNewAction,"Create a new graph.","control N",'N',
      "/images/New16.gif");
    fileSaveAsAction = new AbstractAction("Save As",
      new ImageIcon(getClass().getResource("/images/SaveAs24.gif")))
    {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(org.wilmascope.global.Constants.getInstance().getProperty("DefaultDataPath"));
        chooser.setFileFilter(fileHandler.getFileFilter());
        int returnVal = chooser.showSaveDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          fileHandler.save(chooser.getSelectedFile().getAbsolutePath());
        }
      }
    };
    configAction(fileSaveAsAction,"Select a file name and save the graph.",
      "control A", 'A', "/images/SaveAs16.gif");
    fileSaveAction = new AbstractAction("Save",
      new ImageIcon(getClass().getResource("/images/Save24.gif")))
    {
      public void actionPerformed(ActionEvent e) {
        fileSaveAsAction.actionPerformed(e);
      }
    };
    configAction(fileSaveAction,"Save the graph.","control S",'S',"/images/Save16.gif");
  }
  public JToolBar getToolPanel() {
    JToolBar p = new JToolBar();
    p.add(getFilebar());
    p.add(getToolbar());
    return p;
  }
  private void configAction(
    Action a,
    String desc,
    String acc,
    char mn,
    String smallIcon
  ) {
    a.putValue(Action.SHORT_DESCRIPTION,desc);
    a.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(acc));
    a.putValue(Action.MNEMONIC_KEY,new Integer(Character.getNumericValue(mn)+55));
    a.putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource(smallIcon)));
  }

  private JToolBar getToolbar() {
    JToolBar toolbar = new JToolBar();
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

    JComponent component = (JComponent)toolbar.add(
      new DropDownButtonPanel(addNodeAction,
        ViewManager.getInstance().getNodeViewRegistry()));
    component.setToolTipText("Create a new Node");
    component = (JComponent)toolbar.add(
      new DropDownButtonPanel(addEdgeAction,
        ViewManager.getInstance().getEdgeViewRegistry()));
    component.setToolTipText("Create a new Edge");
    component = (JComponent)toolbar.add(
      new DropDownButtonPanel(addClusterAction,
        ViewManager.getInstance().getClusterViewRegistry()));
    component.setToolTipText("Create a new Cluster");
    toolbar.add(pickableClusterAction).setToolTipText("Make all Clusters pickable");
    toolbar.add(showHiddenAction).setToolTipText("Show hidden objects");
    toolbar.add(adjustForcesAction).setToolTipText("Adjust root Cluster Forces");
    return toolbar;
  }
  private JToolBar getFilebar() {
    JToolBar filebar = new JToolBar();
    filebar.add(fileNewAction).setToolTipText("New Graph");
    filebar.add(fileOpenAction).setToolTipText("Open graph from file");
    filebar.add(fileSaveAction).setToolTipText("Save graph to a file");
    return filebar;
  }
  public JMenu getEditMenu() {
    JMenu editMenu = new JMenu();
    editMenu.add(addNodeAction);
    editMenu.add(addEdgeAction);
    editMenu.add(addClusterAction);
    editMenu.add(pickableClusterAction);
    editMenu.add(showHiddenAction);
    editMenu.add(adjustForcesAction);
    editMenu.add(graphOperationsAction);
    return editMenu;
  }
  public JMenu getFileMenu() {
    JMenu fileMenu = new JMenu();
    fileMenu.add(fileNewAction);
    fileMenu.add(fileOpenAction);
    fileMenu.add(fileSaveAction);
    fileMenu.add(fileSaveAsAction);
    return fileMenu;
  }
  private static Actions instance = new Actions();
  public static Actions getInstance() {
    return instance;
  }
}
