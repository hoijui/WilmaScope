package org.wilmascope.forcelayout;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import javax.vecmath.Point3f;
import org.wilmascope.graph.Node;
public class LevelConstraint extends Constraint {

  public LevelConstraint(int level, float separation) {
    this.level = level;
    this.separation = separation;
  }

  public void apply(NodeForceLayout n) {
    Node node = n.getNode();
    Point3f position = node.getPosition();
    Point3f clusterCentroid = node.getOwner().getPosition();
    position.z = clusterCentroid.z + (float)level * separation;
  }

  int level;
  float separation;
}