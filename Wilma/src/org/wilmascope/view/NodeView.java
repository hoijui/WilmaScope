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

package org.wilmascope.view;

import org.wilmascope.graph.Node;

import javax.vecmath.*;
import javax.swing.ImageIcon;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.behaviors.mouse.*;
import java.util.Properties;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

public abstract class NodeView extends GraphElementView
implements org.wilmascope.graph.NodeView {

  public NodeView() {
  }
  /*
  public void draw() {
    setTranslation(new Vector3f(node.getPosition()));
  }
  */
  public void draw() {
    Node n = getNode();
    double radius = (double)n.getRadius();
    setResizeTranslateTransform(
      new Vector3d(radius,radius,radius),
      new Vector3f(n.getPosition()));
  }
  public Node getNode() {
    return node;
  }
  public void setNode(Node node) {
    this.node = node;
  }
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("/images/node.png"));
  }
  protected void showLabel(String text) {
    double r = (double)node.getRadius();
    // the log term on the scale is a feable attempt to stop labels getting too
    // big with large clusters... however it doesn't really work because clusters
    // typically grow or shrink after a label is set... when the label will be
    // scaled linearly along with the cluster.  A more correct solution would be
    // to have the label branch scaled separately in the draw method.
    addLabel(text, 10d / Math.log(r * 10 * Math.E), new Point3f(0.0f,0.05f,-0.07f), new Vector3f(-0.1f * (float)text.length(),1.3f,0.0f), getAppearance());
  }
  BranchGroup anchorBranch;
  public void showAnchor() {
    Appearance a = new Appearance();
    a.setMaterial(Colours.blueMaterial);
    Cone pin = new Cone(0.05f,0.1f,a);
    makePickable(pin.getShape(Cone.BODY));
    makePickable(pin.getShape(Cone.CAP));
    anchorBranch = new BranchGroup();
    anchorBranch.setCapability(BranchGroup.ALLOW_DETACH);
    Transform3D t = new Transform3D();
    t.set(new Vector3f(0,-node.getRadius(),0));
    TransformGroup tg = new TransformGroup(t);
    Transform3D r = new Transform3D();
    r.setRotation(new AxisAngle4f(-1f,0f,1f,3f*(float)Math.PI/4f));
    TransformGroup rg = new TransformGroup(r);
    tg.addChild(pin);
    rg.addChild(tg);
    anchorBranch.addChild(rg);
    addLiveBranch(anchorBranch);
  }
  public void hideAnchor() {
    anchorBranch.detach();
  }

  /**
   * Move the node in a plane parallel to the view plate, such that it appears
   * at the point on the canvas specified in x and y.
   * The x and y parameters are in AWT coordinates with 0,0 at the top left
   * of the canvas.
   * @param c the canvas
   * @param x the x awt coordinate
   * @param y the y awt coordinate
   */
  public void moveToCanvasPos(GraphCanvas c, int x, int y) {
    // All transformations are done in VWorld coordinates so set up the
    // transforms necessary to convert
    Transform3D localToVworld = new Transform3D();
    Transform3D imagePlateToVworld = new Transform3D();
    c.getImagePlateToVworld(imagePlateToVworld);
    getTransformGroup().getLocalToVworld(localToVworld);

    // get current node position and convert to VWorld
    Point3f pos = new Point3f(node.getPosition());
    localToVworld.transform(pos);

    // get eye position
    Point3d d = new Point3d();
    c.getCenterEyeInImagePlate(d);
    imagePlateToVworld.transform(d);
    Point3f eyePos = new Point3f(d);

    // get mouse position
    c.getPixelLocationInImagePlate(x,y,d);
    imagePlateToVworld.transform(d);
    Point3f mousePos = new Point3f(d);

    // calculate vector from eye to canvas
    Vector3f eyeToCanvas = new Vector3f();
    eyeToCanvas.sub(mousePos,eyePos);

    // the target position will be at a point in the same plane parallel to
    // the view plate.  Calculate the scale factor for eye to canvas in order
    // to place the node from the known z values.
    float t = (pos.z - mousePos.z) / eyeToCanvas.z;

    pos.scale(t,eyeToCanvas);
    pos.add(mousePos);

    // If this method turns out to be too slow we could easily speed it up
    // by calculating the transforms in advance.

    // move the node
    localToVworld.invert();
    localToVworld.transform(pos);
    node.setPosition(pos);
  }
  public void setProperties(Properties p) {
    super.setProperties(p);
    String radius = p.getProperty("Radius");
    if(radius!=null) {
      getNode().setRadius(Float.parseFloat(radius));
    }
  }
  public Properties getProperties() {
    Properties p = super.getProperties();
    p.setProperty("Radius",""+getNode().getRadius());
    return p;
  }
  private Node node;
}
