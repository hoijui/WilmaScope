package org.wilmascope.forcelayout;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Quat4f;
import javax.vecmath.AxisAngle4f;
import javax.media.j3d.Transform3D;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.NodeList;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.Edge;
public class Planar extends Force {

  public Planar(float strength) {
    super(strength, "Planar");
  }
  public void setCluster(Cluster root) {
    this.root = root;
    nodes = root.getNodes();
    externalEdges = root.getEdges();
    normal = root.getNormal();
    orientation.set(new AxisAngle4f(normal,0));
  }
  public void calculate() {
    Point3f centroid = root.getPosition();
    /*
    Point3f barycenter = nodes.getBarycenter();
    normal.sub(centroid, barycenter);
    if(normal.length() < 0.0001) {
      normal.set(Constants.minVector);
    }
    */
    Vector3f netTorque = new Vector3f();
    Vector3f f = new Vector3f();
    for(int i=0; i<nodes.size(); i++) {
      // find f: force on the node back towards the plane and an equal and
      // opposite force back on the plane
      Node n = nodes.get(i);
      f.sub(n.getPosition(), centroid);
      float d = f.dot(normal);
      f.set(normal);
      f.scale(d);

      // find p: the closest point on the plane to the node
      Point3f p = new Point3f();
      p.add(n.getPosition(),f);

      f.scale(6 * strengthConstant);
      NodeForceLayout l = (NodeForceLayout)n.getLayout();
      //l.subForce(f);
      //((NodeForceLayout)root.getLayout()).addForce(f);

      // calculate a torque on the plane due to the force
      //   torque = r X F
      Vector3f r = new Vector3f();
      // alternative: r.sub(n.getPosition(),centroid);
      r.sub(p,centroid);
      Vector3f torque = new Vector3f();
      torque.cross(r,f);
      netTorque.add(torque);
    }
    /*
    Vector3f norm = new Vector3f();
    norm.cross(vY,normal);
    root.rotate(new AxisAngle4f(norm.x,norm.y,norm.z,vY.angle(norm)));
    */

    /*
    float angleDelta = strengthConstant * netTorque.length()/(10);
    netTorque.normalize();
    if(angleDelta > 0.00001) {
      root.rotate(new AxisAngle4f(netTorque, angleDelta));
    }
    rotate();
    */
  }
  private void rotate() {
    Transform3D t = new Transform3D();
    t.setRotation(root.getOrientation());
    normal.set(vY);
    t.transform(normal);
  }
  NodeList nodes;
  Cluster root;
  EdgeList externalEdges;
  Vector3f normal = new Vector3f();
  Vector3f vY = new Vector3f(0,1f,0);
  // an angle by which the Cluster and all its contents will be rotated at the
  // next draw
  private Quat4f orientation = new Quat4f();
}
