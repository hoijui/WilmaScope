package org.wilmascope.columnlayout;

import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;
import javax.vecmath.Point3f;
import java.util.Iterator;
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
  * as the root cluster but with the z determined by each node's level
  */
public class ColumnLayout implements LayoutEngine {

    public ColumnLayout(Cluster root) {
        this.root = root;
        root.setLayoutEngine(this);
        levelSeparation = 0.2f;
    }

    public void calculateLayout() {
    }
    public boolean applyLayout() {
        Point3f rootPosition = root.getPosition();
        NodeList nodes = root.getNodes();
        root.setMass(1f);
        for(nodes.resetIterator(); nodes.hasNext();) {
            Node node = nodes.nextNode();
            Point3f position = node.getPosition();
            NodeColumnLayout nodeLayout = (NodeColumnLayout)node.getLayout();
            position.x = rootPosition.x;
            position.y = rootPosition.y;
            position.z = rootPosition.z + levelSeparation * nodeLayout.getLevel();
        }
        return true;
    }
    public void setBalanced(boolean balanced) {
        /**@todo: Implement this org.wilmascope.graph.LayoutEngine method*/
        throw new java.lang.UnsupportedOperationException("Method setBalanced() not yet implemented.");
    }
    public org.wilmascope.graph.NodeLayout createNodeLayout() {
        return new NodeColumnLayout(nextLevel++);
    }
    public org.wilmascope.graph.EdgeLayout createEdgeLayout() {
        return new EdgeColumnLayout();
    }
    public void setBaseLevel(int level){
        baseLevel = nextLevel = level;
    }
    public int getBaseLevel() {
      return baseLevel;
    }
    public int getNextLevel() {
        return nextLevel;
    }
    public void reset() {}
    public void decrementLevels() {
        NodeList nodes = root.getNodes();
        for(nodes.resetIterator(); nodes.hasNext();) {
            Node node = nodes.nextNode();
            NodeColumnLayout nodeLayout = (NodeColumnLayout)node.getLayout();
            nodeLayout.setLevel(nodeLayout.getLevel() - 1);
        }
    }
    Cluster root;
    float levelSeparation;
    int nextLevel = 0;
    int baseLevel = 0;
}