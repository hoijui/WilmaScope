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
import org.wilmascope.chat.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

  class GraphElementServant extends _GraphElementStub {
    GraphElementServant(GraphControl.GraphElementFacade element) {
      this.element = element;
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
      element.show();
    }
    /**
     * set the colour of the component
     * @param c a structure containing red, green and blue float elements
     */
    public void setColour(Colour c) {
      element.setColour(c.red,c.green,c.blue);
    }
    /**
     * set the label to show with the element
     * @param label the label to show
     */
    public void setLabel(String label) {
      element.setLabel(label);
    }
    /**
     * set a PickingClient whose callback method will be called when the
     * element is selected by picking with the mouse
     */
    public void setPickingClient(final PickingClient client) {
      element.addPickingClient(new org.wilmascope.view.PickingClient() {
        public void callback(java.awt.event.MouseEvent e) {
          client.callback(e.getX(),e.getY(),(short)0);
        }
      });
    }
    /**
     * delete the element, removing all references to it... forever!
     */
    public void delete() {
      element.delete();
    }
    GraphControl.GraphElementFacade element;
  }
  /**
   * The interface for edges
   */
  class EdgeServant extends GraphElementServant implements EdgeOperations {
    /**
     * create a new facade for a predefined edge
     * @param edge the predefined edge: note that the edge must have its view set
     */
    EdgeServant(GraphControl.EdgeFacade edge) {
      super(edge);
      this.edge = edge;
    }
    public void setRelaxedLength(float l) {
      edge.setRelaxedLength(l);
    }
    GraphControl.EdgeFacade getEdge() {
      return edge;
    }
    private GraphControl.EdgeFacade edge;
  }

  /**
   * the interface for nodes
   */
  class NodeServant extends GraphElementServant implements NodeOperations {
    /**
     * create a new facade for a predefined node
     * @param n the predefined node: note that the node must have its view set
     */
    NodeServant(GraphControl.NodeFacade n) {
      super(n);
      node = n;
    }
    /**
     * set the radius of the node
     * @param radius the radius
     */
    public void setRadius(float radius) {
      node.setRadius(radius);
    }
    public void setLevelConstraint(int level) {
      node.setLevelConstraint(level);
    }
    GraphControl.NodeFacade getNode() {
      return node;
    }
    private GraphControl.NodeFacade node;
  }

  /**
   * the interface for clusters which are collections of nodes,
   * note that a cluster is also a node so you can add a cluster as a member
   * to a cluster using the {@link #addNode(Node)} method
   */
  class ClusterServant extends NodeServant implements ClusterOperations {
    // if only there were a getImpl() method in the _Tie classes I wouldn't
    // need the following!
    class NodeTie extends Node_Tie {
      NodeServant servant;
      NodeTie(NodeServant n) {
        super(n);
        this.servant = n;
      }
      NodeServant getServant() {
        return servant;
      }
    }
    ClusterServant(GraphControl.ClusterFacade c) {
      super(c);
      cluster = c;
    }
    /**
     * Add a pre-existing node to the cluster
     */
    public void addNode(Node n) {
      cluster.addNode(((NodeTie)n).getServant().getNode());
    }
    public Cluster addNewCluster() {
      ClusterServant servant = new ClusterServant(cluster.addNewCluster());
      return (Cluster)new Cluster_Tie(servant);
    }
    /**
     * create a new node and add it to the cluster
     */
    public Node addNewNode() {
      NodeServant servant = new NodeServant(cluster.addNode());
      return (Node)new NodeTie(servant);
    }
    /**
     * Create a new adge between two nodes and add it to the cluster
     */
    public Edge addNewEdge(Node start, Node end) {
      EdgeServant servant = new EdgeServant(
        cluster.addEdge(
          ((NodeTie)start).getServant().getNode(),
          ((NodeTie)end).getServant().getNode()));
      return (Edge)new Edge_Tie(servant);
    }
    /**
     * Create a new cluster and add it as a member of this cluster
     */
    public Cluster addCluster() {
      ClusterServant servant = new ClusterServant(cluster.addCluster());
      return (Cluster)new Cluster_Tie(servant);
    }
    /**
     * remove a node from the cluster
     * Doesn't delete, to delete the node use node.delete() method
     */
    public void removeNode(Node n) {
      cluster.removeNode(((NodeTie)n).getServant().getNode());
    }
    /**
     * set a BalancedEventClient whose callback method will be called when the
     * graph becomes balanced
     */
    public void setBalancedEventClient(final BalancedEventClient client) {
      cluster.setBalancedEventClient(new org.wilmascope.forcelayout.BalancedEventClient() {
        public void callback() {
          client.callback();
        }
      });
    }
    public void freeze() {
      cluster.freeze();
    }

    public void unfreeze() {
      cluster.unfreeze();
    }
    public void setBalancedThreshold(float threshold) {
      cluster.setBalancedThreshold(threshold);
    }
    public Force addForce(String name) {
      try {
        return new ForceServant(cluster.addForce(name));
      } catch(Exception e) {
        System.out.println("Couldn't addForce because: "+e.getMessage());
        return null;
      }
    }

    GraphControl.ClusterFacade getCluster() {
      return cluster;
    }
    private GraphControl.ClusterFacade cluster;
  }
  class ForceServant extends _ForceStub {
    ForceServant(GraphControl.ForceFacade f) {
      this.force = f;
    }
    public void setStrength(float strength) {
      force.setStrength(strength);
    }
    private GraphControl.ForceFacade force;
  }

class GraphControlServant extends _GraphControlStub {
  GraphControlServant(GraphControl gc) {
    this.gc = gc;
  }
  /**
   * get a reference to the root cluster of the graph
   */
  public Cluster getRootCluster() {
    ClusterServant servant = new ClusterServant(gc.getRootCluster());
    return (Cluster)new Cluster_Tie(servant);
  }
  public void setRootCluster(Cluster rootCluster) {
    gc.setRootCluster(((ClusterServant)rootCluster).getCluster());
  }

  private GraphControl gc;
}

public class WilmaChatServCORBA {
  public static void start(String[] args, GraphControl gc) {
    try {
      //String[] args2 = {"-ORBInitialHost","old-trafford",
                          //"-ORBInitialPort","12345"};
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // create servant and register it with the ORB
      GraphControlServant graphControl = new GraphControlServant(gc);
      orb.connect(graphControl);
      // get the root naming context
      org.omg.CORBA.Object objRef =
      orb.resolve_initial_references("NameService");
      NamingContext ncRef = NamingContextHelper.narrow(objRef);

      // bind the Object Reference in Naming
      NameComponent nc = new NameComponent("WilmaGraph", "Object");
      NameComponent path[] = {nc};
      ncRef.rebind(path, graphControl);

      // wait for invocations from clients
      java.lang.Object sync = new java.lang.Object();
      synchronized (sync) {
        sync.wait();
      }
    } catch (Exception e) {
    System.err.println("ERROR: " + e);
    e.printStackTrace(System.out);
    }
  }
}
