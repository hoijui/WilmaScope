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
public class MultiScaleLayout extends LayoutEngine {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.layoutregistry.LayoutPrototype#getTypeName()
	 */
	public String getTypeName() {
		return "Multiscale";
	}
	public void init(Cluster root) {
		super.init(root);
		qGraph = new QuickGraph();
	}
	QuickGraph qGraph;
	boolean reset = true;
	/**
	 * calculate the changes required to move the graph to a nicer layout
	 */
	public void calculateLayout() {
		if (reset && getRoot().getNodes().size() > 1) {
			qGraph = new QuickGraph();
			reset = false;
			NodeList nodes = getRoot().getNodes();
			EdgeList edges = getRoot().getInternalEdges();
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
			NodeList nodes = getRoot().getNodes();
			for (Node n : nodes) {
        Point3f p = new Point3f(((MultiScaleNodeLayout) n.getLayout()).position);
        p.scale(scale);
        p.add(getRoot().getPosition());
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
		return new MSParamsPanel((GraphControl.Cluster) getRoot().getUserFacade());
	}
	public static float scale = 1f / 10f;

	public String getName() {
		return "Multiscale";
	}

}
