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

import org.wilmascope.view.ViewManager;
import org.wilmascope.view.GraphCanvas;
import org.wilmascope.view.BehaviorClient;
import org.wilmascope.forcelayout.ForceManager;
import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.forcelayout.NodeForceLayout;
import org.wilmascope.forcelayout.EdgeForceLayout;
import org.wilmascope.forcelayout.Repulsion;
import org.wilmascope.forcelayout.Spring;
import org.wilmascope.forcelayout.Origin;
import org.wilmascope.forcelayout.DirectedField;
import org.wilmascope.forcelayout.Force;
import org.wilmascope.forcelayout.BalancedEventClient;
import org.wilmascope.graph.GraphElement;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.NodeList;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.view.GraphElementView;
import org.wilmascope.view.ClusterView;
import org.wilmascope.view.NodeView;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.PickingClient;
import java.util.Vector;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

/**
 * GraphControl gives the access point or facade for the WilmaGraph drawing
 * engine.
 */
public class GraphControl {
  // In {@link PickListener} we have to do what amounts to run-time type
  // checking so I figured why not pass around java.lang.Class variables.
  // In hindsight it probably wasn't the easiest or most elegant way of
  // doing it, but it works, it's type safe (isn't it?) and it was an
  // interesting excercise, so it can stay.
  {
    edgeClass = (new EdgeFacade()).getClass();
    clusterClass = (new ClusterFacade(false)).getClass();
    nodeClass = (new NodeFacade(false)).getClass();
    // GraphElementFacade is abstract so we have to get the class as follows:
    graphElementClass = nodeClass.getSuperclass();
  }
  /** an instance of a {@link GraphElementFacade} class that can
   *  be passed into {@link PickListener#enableMultiPicking}
   *  and {@link PickListener#setSinglePickClient}
   */
  public static Class graphElementClass;
  /** an instance of a {@link NodeFacade} class that can
   *  be passed into {@link PickListener#enableMultiPicking}
   *  and {@link PickListener#setSinglePickClient}
   */
  public static Class nodeClass;
  /** an instance of a {@link EdgeFacade} class that can
   *  be passed into {@link PickListener#enableMultiPicking}
   *  and {@link PickListener#setSinglePickClient}
   */
  public static Class edgeClass;
  /** an instance of a {@link ClusterFacade} class that can
   *  be passed into {@link PickListener#enableMultiPicking}
   *  and {@link PickListener#setSinglePickClient}
   */
  public static Class clusterClass;
  /**
   * the basic interface common to all GraphElements
   */
  public abstract class GraphElementFacade {
    /**
     * create a GraphElement with no view or layout
     */
    GraphElementFacade() {}
    GraphElementFacade(GraphElement element) {
      this.element = element;
      element.setUserFacade(this);
    }
    void initView(GraphElementView view) {
      graphElementView = view;
      graphElementView.initGraphElement();
      if(label!=null) {
        setLabel(label);
      }
      pickListener.register(this);
    }
    /**
     * This will hide the element
     * Clusters will have their view hidden but their constituents will still
     * be visible (if they are already)
     */
    public void hide() {
      element.hide();
    }
    /**
     * Make element visible
     */
    public void show() {
      element.show(graphCanvas);
    }
    /**
     * set the colour of the component
     * @param red level of red (0-1f)
     * @param blue level of blue (0-1f)
     * @param green level of green (0-1f)
     */
    public void setColour(float red, float green, float blue) {
      graphElementView.setColour(red,green,blue);
    }
    public void setColour(java.awt.Color colour) {
      graphElementView.setColour(colour);
    }
    public java.awt.Color getColour() {
      return graphElementView.getColour();
    }
    /**
     * revert to default colour
     */
    public void defaultColour() {
      graphElementView.defaultColour();
    }
    public boolean isDefaultColour() {
      return graphElementView.isDefaultColour();
    }
    public java.awt.Color getDefaultColour() {
      return graphElementView.getDefaultColour();
    }
    /**
     * set to highlight colour
     */
    public void highlightColour() {
      graphElementView.highlightColour();
    }
    /**
     * set the label to show with the element
     * @param label the label to show
     */
    public void setLabel(String label) {
      this.label = label;
      graphElementView.setLabel(label);
    }
    public String getLabel() {
      return label;
    }
    public void setPickable(boolean pickable) {
      graphElementView.setPickable(pickable);
    }
    /**
     * set a PickingClient whose callback method will be called when the
     * element is selected by picking with the mouse
     */
    public void addPickingClient(PickingClient client) {
      graphElementView.addPickingClient(client);
    }
    public Object getUserData() {
      return graphElementView.getUserData();
    }
    public void setUserData(Object data) {
      graphElementView.setUserData(data);
    }
    public String getViewType() {
      return graphElementView.getTypeName();
    }
    public GraphElementView getView() {
      return graphElementView;
    }
    /**
     * delete the element, removing all references to it... forever!
     */
    public void delete() {
      if(element instanceof Edge) {
        allEdges.remove((Edge)element);
      } else {
        allEdges.removeAll(((Node)element).getEdges());
        allNodes.remove((Node)element);
      }
      if(element instanceof Cluster) {
        NodeList nodes = ((Cluster)element).getAllNodes();
        allEdges.removeAll(nodes.getEdges());
        allNodes.removeAll(nodes);
      }
      element.delete();
    }
    private String label;
    private GraphElementView graphElementView;
    private GraphElement element;
  }

  /**
   * The interface for edges
   */
  public class EdgeFacade extends GraphElementFacade {
    EdgeFacade() {}
    /**
     * create a new facade for a predefined edge
     * @param edge the predefined edge: note that the edge must have its view set
     */
    EdgeFacade(Edge edge) {
      super(edge);
      this.edge = edge;
      allEdges.add(edge);
    }
    /**
     * create an edge between two nodes
     * @param start the start node
     * @param end the end node
     */
    EdgeFacade(NodeFacade start, NodeFacade end) {
      this(new Edge(start.getNode(),end.getNode()));
      edge.setLayout(forceManager.createEdgeLayout());
      edge.recalculate();
      this.start = start;
      this.end = end;
    }
    public void setView(EdgeView view) {
      // we use hide() to remove the old view from the scene graph and set
      // visible to false, (ie so that show() will actually do something)
      hide();
      edge.setView(view);
      initView(view);
      show();
    }
    /**
     * set the natural or unstretched or compacted length of the edge
     * @param length the new natural length for the edge
     */
    public void setRelaxedLength(float length) {
      ((EdgeForceLayout)edge.getLayout()).setRelaxedLength(length);
    }

    public void reverseDirection() {
      edge.reverseDirection();
      NodeFacade n = start;
      start = end;
      end = n;
    }
    public NodeFacade getStartNode() {
      return start;
    }
    public NodeFacade getEndNode() {
      return end;
    }
    /**
     * get the edge underlying this facade
     */
    public Edge getEdge() {
      return edge;
    }
    private Edge edge;
    private NodeFacade start, end;
  }

  /**
   * the interface for nodes
   */
  public class NodeFacade extends GraphElementFacade {
    /**
     * create a new facade for a predefined node
     * @param n the predefined node: note that the node must have its view set
     */
    NodeFacade(Node n) {
      super(n);
      node = n;
      allNodes.add(n);
    }
    /** create a dummy nodeFacade */
    NodeFacade(boolean dummy) {}
    /**
     * create a new node
     */
    NodeFacade() {
      this(new Node());
      node.setLayout(forceManager.createNodeLayout());
    }
    public void setPosition(javax.vecmath.Point3f pos) {
      node.setPosition(pos);
    }
    public javax.vecmath.Point3f getPosition() {
      return node.getPosition();
    }
    public void setView(NodeView view) {
      hide();
      node.setView(view);
      initView(view);
      show();
    }
    public float getMass() {
      return node.getMass();
    }
    public void setRadius(float radius) {
      node.setRadius(radius);
    }
    public float getRadius() {
      return node.getRadius();
    }
    public void setFixedPosition(boolean fixed) {
      node.setFixedPosition(fixed);
      if(fixed){
        ((NodeView)node.getView()).showAnchor();
      } else {
        ((NodeView)node.getView()).hideAnchor();
      }
    }
    public boolean isFixedPosition() {
      return node.isFixedPosition();
    }
    public void moveToCanvasPos(int x, int y) {
      ((NodeView)node.getView()).moveToCanvasPos(graphCanvas,x,y);
    }
    public int getDegree() {
      return node.getDegree();
    }
    /**
     * get the node underlying this facade
     */
    protected Node getNode() {
      return node;
    }
    private Node node;
  }
  /**
   * the interface for clusters which are collections of nodes,
   * note that a cluster is also a node so you can add a cluster as a member
   * to a cluster using the {@link #addNode(GraphControl.NodeFacade)} method
   */
  public class ClusterFacade extends NodeFacade {
    GraphControl gc;
    /**
     * dummy constructor for creating dummy cluster objects, don't use.
     */
    ClusterFacade(boolean dummy) {
      super(false);
    }
    ClusterFacade(GraphControl gc, Cluster c) {
      super((Node)c);
      cluster = c;
      this.gc = gc;
    }
    ClusterFacade(GraphControl gc, NodeView view) {
      this(gc, new Cluster(view));
      initView(view);
      this.gc = gc;
      ForceLayout layoutEngine = new ForceLayout(cluster);
      layoutEngine.setIterations(1);
      if(view.getTypeName().equals("Planar Cluster")) {
        layoutEngine.setConstrained();
      }
      cluster.setLayoutEngine(layoutEngine);
      NodeForceLayout layout = (NodeForceLayout)forceManager.createNodeLayout();
      cluster.setLayout(layout);
      show();
    }
    ClusterFacade(GraphControl gc, String viewType) throws ViewManager.UnknownViewTypeException {
      this(gc, ViewManager.getInstance().createClusterView(viewType));
    }
    ClusterFacade(GraphControl gc) throws ViewManager.UnknownViewTypeException{
      this(gc, ViewManager.getInstance().createClusterView());
    }
    public ClusterFacade addNewCluster() {
      synchronized(gc) {
      try {
        ClusterFacade c = new ClusterFacade(gc);
        cluster.addNode(c.getCluster());
        return c;
      } catch (ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    /**
     * deprecated use ((ForceLayout)getLayoutEngine).addForce() instead
     */
    public ForceFacade addForce(String name)
    throws ForceManager.UnknownForceTypeException {
      synchronized(gc) {
        ForceLayout layout = (ForceLayout)cluster.getLayoutEngine();
        Force f = forceManager.createForce(name);
        layout.addForce(f);
        ForceFacade forceFacade = new ForceFacade(f);
        return forceFacade;
      }
    }
    public ForceFacade[] getForces() {
      Vector forces = new Vector();
      ForceLayout layout = (ForceLayout)cluster.getLayoutEngine();
      Vector fs = layout.getForces();
      for(int i=0;i<fs.size();i++) {
        forces.add(new ForceFacade((Force)fs.get(i)));
      }
      return (ForceFacade[])forces.toArray(new ForceFacade[0]);
    }
    public void removeAllForces() {
      ForceLayout layout = (ForceLayout)cluster.getLayoutEngine();
      layout.removeAllForces();
    }
    public void deleteAll() {
      NodeList condemned = new NodeList(cluster.getNodes());
      for(int i=0; i<condemned.size(); i++) {
        NodeFacade n = (NodeFacade)condemned.get(i).getUserFacade();
        n.delete();
      }
    }

    public org.wilmascope.graph.LayoutEngine getLayoutEngine() {
      return cluster.getLayoutEngine();
    }

    /**
     * Add a pre-existing node to the cluster
     */
    public void addNode(NodeFacade n) {
      synchronized(gc) {
      Node node = n.getNode();
      NodeForceLayout nodeLayout = (NodeForceLayout)node.getLayout();
      cluster.addNode(node);
      NodeForceLayout clusterLayout = (NodeForceLayout)cluster.getLayout();
      }
    }
    public void add(GraphElementFacade e) {
      synchronized(gc) {
      if(e instanceof NodeFacade) {
        addNode((NodeFacade)e);
      } else if (e instanceof EdgeFacade) {
        addEdge((EdgeFacade)e);
      }
      }
    }
    public NodeFacade addNode() {
      synchronized(gc) {
      try {
        return addNode(ViewManager.getInstance().createNodeView());
      } catch (ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    public NodeFacade addNode(String nodeType) {
      synchronized(gc) {
      try {
        return addNode(ViewManager.getInstance().createNodeView(nodeType));
      } catch (ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    /**
     * create a new node and add it to the cluster
     */
    public NodeFacade addNode(NodeView view) {
      synchronized(gc) {
      NodeFacade n = new NodeFacade();
      n.setView(view);
      addNode(n);
      return n;
      }
    }
    /**
     * Add a pre-existing edge to a cluster
     */
    private void addEdge(EdgeFacade e) {
      synchronized(gc) {
      cluster.addEdge(e.getEdge());
      }
    }
    /**
     * Create a new edge between two nodes and add it to the cluster
     * @param start the start node
     * @param end the end node
     * @param view the view to use for the edge
     * @return the new edge
     */
    public EdgeFacade addEdge(
      NodeFacade start,
      NodeFacade end,
      EdgeView view)
    {
      synchronized(gc) {
      EdgeFacade e = new EdgeFacade(start, end);
      e.setView(view);
      addEdge(e);
      return e;
      }
    }
    /**
     * Add a new edge
     * @param start the start node
     * @param end the end node
     * @param edgeType the type of edge to add, from the ViewManager
     * @return the new Edge
     */
    public EdgeFacade addEdge(
      NodeFacade start,
      NodeFacade end,
      String edgeType)
    {
      synchronized(gc) {
      try {
        EdgeView view = ViewManager.getInstance().createEdgeView(edgeType);
        return addEdge(start, end, view);
      } catch(ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    public EdgeFacade addEdge(
      NodeFacade start,
      NodeFacade end,
      String edgeType,
      float radius)
    {
      synchronized(gc) {
      try {
        EdgeView view = ViewManager.getInstance().createEdgeView(edgeType);
        view.setRadius(radius);
        return addEdge(start, end, view);
      } catch(ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    /**
     * Add the new edge of the default type to the cluster
     * @param start the start node
     * @param end the end node
     * @return the new edge
     */
    public EdgeFacade addEdge(NodeFacade start, NodeFacade end) {
      synchronized(gc) {
      try {
        EdgeView view = ViewManager.getInstance().createEdgeView();
        return addEdge(start, end, view);
      } catch(ViewManager.UnknownViewTypeException ex) {
        ex.printStackTrace();
        return null;
      }
      }
    }
    /**
     * Create a new cluster and add it as a member of this cluster
     */
    public ClusterFacade addCluster() {
      synchronized(gc) {
      try {
        ClusterFacade c = new ClusterFacade(gc);
        addNode(c);
        return c;
      } catch(ViewManager.UnknownViewTypeException e) {
        throw new Error("No default ClusterView type set!??!?!");
      }
      }
    }
    /**
     * Create a new cluster and add it as a member of this cluster
     */
    public ClusterFacade addCluster(String viewType) {
      synchronized(gc) {
      try {
        ClusterFacade c = new ClusterFacade(gc, viewType);
        addNode(c);
        return c;
      } catch(ViewManager.UnknownViewTypeException e) {
        System.err.println("Couldn't find view type: "+viewType
          + "in the repository, using default");
        return addCluster();
      }
      }
    }
    /**
     * Create a new cluster and add it as a member of this cluster
     */
    public ClusterFacade addCluster(Vector nodeFacades) {
      synchronized(gc) {
      try {
        ClusterFacade c = new ClusterFacade(gc);
        addNode(c);
        NodeList nodes = new NodeList();
        for(int i=0; i<nodeFacades.size(); i++) {
          nodes.add(((NodeFacade)nodeFacades.get(i)).getNode());
        }
        c.getCluster().addNodes(nodes);
        return c;
      } catch(ViewManager.UnknownViewTypeException e) {
        throw new Error();
      }
      }
    }
    /**
     * remove a node from the cluster
     * Doesn't delete, to delete the node use node.delete() method
     */
    public void removeNode(NodeFacade n) {
      cluster.removeNode(n.getNode());
    }
    /**
     * remove an edge from the cluster
     * Doesn't delete, to delete the node use {@link GraphElement#delete()} method
     */
    public void removeEdge(EdgeFacade e) {
      cluster.removeEdge(e.getEdge());
    }
    public void remove(GraphElementFacade e) {
      if(e instanceof NodeFacade) {
        removeNode((NodeFacade)e);
      } else if (e instanceof EdgeFacade) {
        removeEdge((EdgeFacade)e);
      }
    }
    public void expand() {
      cluster.expand(graphCanvas);
      ((ClusterView)cluster.getView()).setExpandedView();
    }
    public void collapse() {
      cluster.collapse();
      ((ClusterView)cluster.getView()).setCollapsedView();
    }
    /**
     * @return true if the cluster is expanded else false
     */
    public boolean isExpanded() {
      return cluster.isExpanded();
    }
    /**
     * set the callback client for when the cluster reaches a balanced state
     * @param client the class whose
     * {@link org.wilmascope.forcelayout.BalancedEventClient#callback} method
     * will be called when the graph is balanced
     */
    public void setBalancedEventClient(BalancedEventClient client) {
      ((ForceLayout)cluster.getLayoutEngine()).setBalancedEventClient(client);
    }
    /**
     * make just the children of this cluster pickable, ie all other graph
     * elements are not pickable, used for removing things from the cluster
     */
    public void makeJustChildrenPickable() {
      setAllPickable(false);
      childrenPickable();
    }
    /**
     * make just the graph elements other than this cluster pickable, used
     * for adding things to the cluster.  Also sets the cluster to the highlight
     * colour to indicate we're doing something to it.
     */
    public void makeNonChildrenPickable() {
      setAllPickable(true);
      setChildrenPickable(false);
      cluster.getView().setPickable(false);
      highlightColour();
    }
    /**
     * makes children pickable... call this method on the rootCluster to reverse
     * the effects of all
     */
    public void childrenPickable() {
      setChildrenPickable(true);
      org.wilmascope.graph.ClusterList childClusters = cluster.getNodes().getClusters();
      for(int i = 0; i < childClusters.size(); i++) {
        ((GraphElementView)childClusters.get(i).getView()).defaultColour();
      }
      cluster.getView().setPickable(false);
      setColour(242/255f,200/255f,242/255f);
    }
    /**
     * sets the pickable status of the child members of this cluster
     */
    private void setChildrenPickable(boolean pickable) {
      NodeList nodes = cluster.getNodes();
      for(int i=0; i<nodes.size(); i++) {
        nodes.get(i).getView().setPickable(pickable);
      }
      EdgeList edges = cluster.getInternalEdges();
      for(int i=0; i<edges.size(); i++) {
        edges.get(i).getView().setPickable(pickable);
      }
    }

    /**
     * kind of dodgy all access method for controlling the pick status of all
     * nodes and edges in the graph, reverts clusters to their default colour
     * (to indicate they are pickable again
     */
    public void setAllPickable(boolean pickable) {
      for(int i = 0; i < allNodes.size(); i++) {
        Node node = allNodes.get(i);
        node.getView().setPickable(pickable);
        if(node instanceof Cluster) {
          ((GraphElementView)node.getView()).defaultColour();
        }
      }
      for(int i = 0; i < allEdges.size(); i++) {
        allEdges.get(i).getView().setPickable(pickable);
      }
    }
    public Cluster getCluster() {
      return cluster;
    }
    /**
     * set all clusters to balanced and cause subsequent clusters to be preset to balanced.
     * Net effect is to freeze the layout animation
     */
    public void freeze() {
      balanced = true;
    }
    /**
     * set all clusters to unbalanced and cause subsequent clusters to be preset to balanced.
     * Net effect is to unfreeze the layout animation
     */
    public void unfreeze() {
      startTime=System.currentTimeMillis();
      layoutIterationsCounter = 0;
      balanced = false;
    }
    public void showHiddenChildren() {
      for(int i = 0; i < allNodes.size(); i++) {
        allNodes.get(i).show(graphCanvas);
      }
      for(int i = 0; i < allEdges.size(); i++) {
        allEdges.get(i).show(graphCanvas);
      }
    }
    public NodeFacade[] getNodes() {
      NodeList nodes = cluster.getNodes();
      NodeFacade[] nodeFacades = new NodeFacade[nodes.size()];
      for(int i=0;i<nodes.size();i++) {
        nodeFacades[i] = (NodeFacade)nodes.get(i).getUserFacade();
      }
      return nodeFacades;
    }
    public EdgeFacade[] getEdges() {
      EdgeList edges = cluster.getInternalEdges();
      EdgeFacade[] edgeFacades = new EdgeFacade[edges.size()];
      for(int i=0;i<edges.size();i++) {
        edgeFacades[i] = (EdgeFacade)edges.get(i).getUserFacade();
      }
      return edgeFacades;
    }
    public void setBalancedThreshold(float threshold) {
      ((ForceLayout)cluster.getLayoutEngine()).setBalancedThreshold(threshold);
      balancedThreshold = threshold;
    }
    public float getBalancedThreshold() {
      return ((ForceLayout)cluster.getLayoutEngine()).getBalancedThreshold();
    }
    public void setIterations(int iterations) {
      ((ForceLayout)cluster.getLayoutEngine()).setIterations(iterations);
    }
    private Cluster cluster;
  }
  public class ForceFacade {
    ForceFacade(Force f) {
      this.force = f;
    }
    public void setStrength(float strength) {
      force.setStrengthConstant(strength);
    }
    public float getStrength() {
      return force.getStrengthConstant();
    }
    public String getType() {
      return force.getTypeName();
    }
    private Force force;
  }
  /**
   * get a reference to the root cluster of the graph
   */
  public ClusterFacade getRootCluster() {
    return rootCluster;
  }
  public void setRootCluster(ClusterFacade rootCluster) {
    this.rootCluster = (ClusterFacade)rootCluster;
    // don't want the root cluster in the list of all nodes...
    allNodes.remove(rootCluster.getCluster());
    rootCluster.hide();
  }
  public void setRootPickingClient(PickingClient client) {
    graphCanvas.setRootPickingClient(client);
  }
  public GraphControl(int xsize, int ysize) {
    viewManager = ViewManager.getInstance();
    forceManager = ForceManager.getInstance();
      // load core views
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.ArrowEdgeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.InheritanceEdgeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.AggregationEdgeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.DefaultClusterView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.ConeClusterView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.BoxClusterView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.DefaultEdgeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.DefaultNodeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.BoxNodeView());
      viewManager.addPrototypeView(new org.wilmascope.viewplugin.LabelNodeView());
      /*
    try {
      viewManager.loadViews(new java.io.File("plugin"),"plugin");
    } catch(java.io.IOException e) {
      System.err.println("Couldn't load plugins because "+e.getMessage());
    }
    */
    try {
      viewManager.getEdgeViewRegistry().setDefaultView("Plain Edge");
      viewManager.getNodeViewRegistry().setDefaultView("DefaultNodeView");
      viewManager.getClusterViewRegistry().setDefaultView("Spherical Cluster");
    } catch(ViewManager.UnknownViewTypeException e) {
      e.printStackTrace();
      System.out.println("Fatal Error, stopping.");
      System.exit(1);
    }
    forceManager.addPrototypeForce(new Repulsion(1.2f,20f));
    forceManager.addPrototypeForce(new Spring(5f));
    forceManager.addPrototypeForce(new Origin(8f));
    forceManager.addPrototypeForce(new DirectedField(1f));
    graphCanvas = new GraphCanvas(xsize, ysize);
    try {
      setRootCluster(new ClusterFacade(this));
    } catch(ViewManager.UnknownViewTypeException ex) {
      ex.printStackTrace();
      throw new Error();
    }
    balancedThreshold = rootCluster.getBalancedThreshold();

    graphCanvas.addPerFrameBehavior(new BehaviorClient() {
      public void callback() {
        iterate();
      }
    });
    graphCanvas.createUniverse();
  }
  protected synchronized void iterate() {
        if(!balanced&&!stalled) {
          for(int i=0;i<1;i++) {
            rootCluster.getCluster().calculateLayout();
            float energy = rootCluster.getCluster().applyLayout();
            layoutIterationsCounter++;
            if(energy < balancedThreshold) {
              balanced = true;
              System.out.println("Balanced after: "+(float)(System.currentTimeMillis()-startTime)/1000f);
              System.out.println("iterations: "+layoutIterationsCounter);
              break;
            }
          }
        }
        rootCluster.getCluster().draw();
  }

  /**
   * brute force method to turn off layout
   */
  public void stall() {
    stalled = true;
  }
  public void unstall() {
    stalled = false;
  }

  public GraphCanvas getGraphCanvas() {
    return graphCanvas;
  }
  private boolean stalled = false;
  private boolean balanced = true;
  private float balancedThreshold = 0.002f;
  private ClusterFacade rootCluster;
  private GraphCanvas graphCanvas;
  private ViewManager viewManager;
  private ForceManager forceManager;
  // The constants
  private static org.wilmascope.global.Constants constants = org.wilmascope.global.Constants.getInstance();
  private NodeList allNodes = new NodeList();
  private EdgeList allEdges = new EdgeList();
  private long startTime = 0;
  private int layoutIterationsCounter = 0;
  public static PickListener getPickListener() {
    return pickListener;
  }
  static PickListener pickListener = new PickListener();
}
