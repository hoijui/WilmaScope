package org.wilmascope.multiscalelayout;
import java.util.Properties;

import javax.swing.JPanel;
import javax.vecmath.Point3f;

import org.wilmascope.control.GraphControl;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.EdgeLayout;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;
import org.wilmascope.graph.NodeList;
/**
 * <p>
 * Description:
 * </p>
 * <p>
 * $Id$
 * </p>
 * <p>
 * 
 * @author</p>
 *         <p>
 * @version $Revision$
 *          </p>
 *          unascribed
 *  
 */
public class MultiScaleLayout implements LayoutEngine {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.layoutregistry.LayoutPrototype#create()
	 */
	public LayoutEngine create() {
		return new MultiScaleLayout();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.layoutregistry.LayoutPrototype#getTypeName()
	 */
	public String getTypeName() {
		return "Multiscale";
	}
	public void init(Cluster root) {
		this.root = root;
		qGraph = new QuickGraph();
	}
	QuickGraph qGraph;
	Cluster root;
	boolean reset = true;
	/**
	 * calculate the changes required to move the graph to a nicer layout
	 */
	public void calculateLayout() {
		if (reset && root.getNodes().size() > 1) {
			qGraph = new QuickGraph();
			reset = false;
			NodeList nodes = root.getNodes();
			EdgeList edges = root.getInternalEdges();
			for (Node n:nodes) {
				n.setLayout(createNodeLayout(n));
			}
			for (Edge e:edges) {
				e.setLayout(createEdgeLayout(e));
			}
			qGraph.setNotConverged();
		}
	}
	/**
	 * apply the changes calculated by {@link #calculateLayout}
	 * 
	 * @return true when a stable state is reached
	 */
	synchronized public boolean applyLayout() {
		boolean done = true;
		if (!reset) { // if reset is still true then graph size <= 1
			done = qGraph.relax();
			NodeList nodes = root.getNodes();
			for (Node n : nodes) {
        Point3f p = new Point3f(((MultiScaleNodeLayout) n.getLayout()).position);
        p.scale(scale);
        p.add(root.getPosition());
				n.setPosition(p);
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
		return new MSParamsPanel((GraphControl.Cluster) root.getUserFacade());
	}
	public static float scale = 1f / 10f;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.graph.LayoutEngine#getProperties()
	 */
	public Properties getProperties() {
		return new Properties();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.graph.LayoutEngine#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties p) {
		// TODO Auto-generated method stub
	}
	public String getName() {
		return "Multiscale";
	}
}
