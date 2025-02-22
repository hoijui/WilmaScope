package plugin;

import org.wilmascope.view.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Cone;

import com.sun.j3d.utils.picking.PickTool;

/**
 * Graphical representation of the edge
 *
 * @author Tim Dwyer
 * @version 1.0
 */

public class DirectedEdgeView extends EdgeView {
  /** radius of the edgeCylinder
   */
  float radius = 0.02f;
  public DirectedEdgeView() {
    setTypeName("Directed Edge");
  }
  protected void setupDefaultMaterial() {
    Material material = new Material();
    material.setDiffuseColor(0.0f, 0.0f, 1.0f);
    material.setAmbientColor(0f,0f,0.4f);
    material.setShininess(50.0f);
    setupDefaultAppearance(material);
  }
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  public void init() {
    Cylinder edgeCylinder = new Cylinder(radius,1);
    addShape(new Shape3D(edgeCylinder.getShape(Cylinder.BODY).getGeometry()));
    showDirectionIndicator();
  }
  public void showDirectionIndicator() {
    Appearance appearance = new Appearance();
    appearance.setMaterial(org.wilmascope.view.Colours.blueMaterial);
    Cone cone=new Cone(0.03f, 0.07f, Cone.GENERATE_NORMALS, appearance);
    Transform3D transform = new Transform3D();
    transform.setTranslation(new Vector3f(0.07f,
      1/4f, 0f));
    TransformGroup coneTransform = new TransformGroup(transform);
    coneTransform.addChild(cone);
    addTransformGroupChild(coneTransform);
  }
  public ImageIcon getIcon() {
    return new ImageIcon("images/directedEdge.png");
  }
}
