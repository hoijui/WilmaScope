package org.wilmascope.multiscalelayout;
import java.util.Properties;

import javax.swing.JPanel;

import org.wilmascope.control.GraphControl;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.EdgeLayout;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;
import org.wilmascope.graph.NodeList;
/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */

public class MultiScaleLayout implements org.wilmascope.graph.LayoutEngine {

  QuickGraph qGraph;
  Cluster root;
  public MultiScaleLayout(org.wilmascope.graph.Cluster root) {
    this.root = root;
    qGraph = new QuickGraph();
  }
  boolean reset = true;
  /**
   * calculate the changes required to move the graph to a nicer layout
   */
  public void calculateLayout() {
    if(reset && root.getNodes().size()>1) {
      qGraph = new QuickGraph();
      reset = false;

      NodeList nodes = root.getNodes();

      EdgeList edges = root.getInternalEdges();
      for(nodes.resetIterator(); nodes.hasNext();) {
        Node n = nodes.nextNode();
        n.setLayout(createNodeLayout(n));

      }
      for(edges.resetIterator(); edges.hasNext();) {
        Edge e = edges.nextEdge();
        e.setLayout(createEdgeLayout(e));
      }
      qGraph.setNotConverged();
    }
  }
  /**
   * apply the changes calculated by
   * {@link #calculateLayout}
   * @return true when a stable state is reached
   */
  synchronized public boolean applyLayout() {
    boolean done = true;
    if(!reset) { // if reset is still true then graph size <= 1
      done = qGraph.relax();
      NodeList nodes = root.getNodes();
      for(nodes.resetIterator(); nodes.hasNext();) {
        Node n = nodes.nextNode();
        n.setPosition(((MultiScaleNodeLayout)n.getLayout()).position);
        n.getPosition().scale(scale);
      }
      reset = done;
    }
    return done;
  }
  public NodeLayout createNodeLayout(Node n) {
    return qGraph.createMultiScaleNodeLayout(n);
  }
  public EdgeLayout createEdgeLayout(Edge e) {
    return qGraph.createMultiScaleEdgeLayout(e);
  }
  public JPanel getControls() {
    return new MSParamsPanel((GraphControl.ClusterFacade)root.getUserFacade());

  }
  public static float scale = 1f/10f;
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#getProperties()
	 */
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties p) {
		// TODO Auto-generated method stub
		
	}
}