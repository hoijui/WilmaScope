package org.wilmascope.columnlayout;

import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;
import javax.vecmath.Point3f;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

/**
 * Column layout simply places each node in the cluster at the same x,y position
 * as the root cluster but with the z determined by each node's stratum
 */
public class ColumnLayout implements LayoutEngine {

	public ColumnLayout(Cluster root) {
		this.root = root;
		root.setLayoutEngine(this);
		strataSeparation = 1f;
	}

	public void calculateLayout() {
	}
	public boolean applyLayout() {
		Point3f rootPosition = root.getPosition();
		NodeList nodes = root.getNodes();
		root.setMass(1f);
		for (nodes.resetIterator(); nodes.hasNext();) {
			Node node = nodes.nextNode();
			Point3f position = node.getPosition();
			NodeColumnLayout nodeLayout = (NodeColumnLayout) node.getLayout();
			position.x = rootPosition.x;
			position.y = rootPosition.y;
			position.z = rootPosition.z + strataSeparation * nodeLayout.getStratum();
		}
		return true;
	}
	public void setBalanced(boolean balanced) {
		/**@todo: Implement this org.wilmascope.graph.LayoutEngine method*/
		throw new java.lang.UnsupportedOperationException(
			"Method setBalanced() not yet implemented.");
	}
	public org.wilmascope.graph.NodeLayout createNodeLayout(Node n) {
		return new NodeColumnLayout(strataCount++);
	}
	public org.wilmascope.graph.EdgeLayout createEdgeLayout(
		org.wilmascope.graph.Edge e) {
		return new EdgeColumnLayout();
	}
	public void setBaseStratum(int stratum) {
		baseStratum = strataCount = stratum;
	}
	public int getBaseStratum() {
		return baseStratum;
	}
	public int getStrataCount() {
		return strataCount;
	}
	public void skipStratum() {
		strataCount++;
	}
	public void reset() {
	}
	Cluster root;
  public void setStrataSeparation(float strataSeparation) {
    this.strataSeparation = strataSeparation;
  }
  public float getStrataSeparation() {
    return strataSeparation;
  }
	private float strataSeparation;
	int strataCount = 0;
	int baseStratum = 0;
}