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

/*
 * GraphCanvas.java
 *
 * Created on 16 April 2000, 17:06
 */

/**
 *
 * @author  $Author$
 * @version $Version:$
 */
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.picking.PickCanvas;
public class GraphCanvas extends Canvas3D {
  protected GraphPickBehavior pb;
  protected TransformGroup transformGroup;
  private BranchGroup bg;
  private Background background;
  private ExponentialFog fog;
  private Constants constants;
  private Bounds bounds;
  private SimpleUniverse universe;
  /** Creates new GraphScene */
  public GraphCanvas(int xsize, int ysize) {    // Create the root of the branch graph
    super(SimpleUniverse.getPreferredConfiguration());
    constants = Constants.getInstance();

    setSize(xsize, ysize);
    setLocation(5, 5);
    bg = new BranchGroup();
    bg.setCapability(BranchGroup.ALLOW_DETACH);
    bg.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    bounds = new BoundingSphere(new Point3d(0,0,0),10000);
    transformGroup = new TransformGroup();
    transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    transformGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);


    // Set up the background
    background = new Background(constants.getColor3f("BackgroundColour"));
    background.setApplicationBounds(bounds);
    background.setCapability(Background.ALLOW_COLOR_WRITE);
    bg.addChild(background);

    // Set up fog
    fog = new ExponentialFog();
    fog.setCapability(ExponentialFog.ALLOW_DENSITY_WRITE);
    fog.setInfluencingBounds(bounds);
    fog.setDensity(constants.getFloatValue("FogDensity"));
    fog.setColor(constants.getColor3f("FogColour"));
    bg.addChild(fog);

    pb = new GraphPickBehavior(bg, (Canvas3D)this, bounds, PickCanvas.GEOMETRY);
    pb.setSchedulingBounds(bounds);
    transformGroup.addChild(pb);
    addLights(bounds);
    addMouseRotators(bounds, transformGroup);
    bg.addChild(transformGroup);
  }
  public Bounds getBoundingSphere() {
    return bounds;
  }
  public void createUniverse() {
    bg.compile();

    // Create a universe with the Java3D universe utility.
    universe = new SimpleUniverse(this);

    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    universe.getViewingPlatform().setNominalViewingTransform();

    universe.addBranchGraph(bg);
  }
  public void setAntialiasingEnabled(boolean enabled) {
    if(getSceneAntialiasingAvailable()) {
      universe.getViewer().getView().setSceneAntialiasingEnable(enabled);
    }
  }
  public void addPerFrameBehavior(BehaviorClient client) {
    GraphBehavior gb = new GraphBehavior(client);
    gb.setSchedulingBounds(bounds);
    bg.addChild(gb);
  }
 private void addLights(Bounds bounds) {
   // Set up ambient light source
   AmbientLight ambientLight = new AmbientLight();
   ambientLight.setInfluencingBounds(bounds);
   ambientLight.setColor(constants.getColor3f("AmbientLightColour"));
   bg.addChild(ambientLight);
   // Set up directional light
   DirectionalLight dirLight = new DirectionalLight();
   dirLight.setInfluencingBounds(bounds);
   Vector3f direction = constants.getVector3f("DirectionalLightVector");
   direction.normalize();
   dirLight.setDirection(direction);
   dirLight.setColor(constants.getColor3f("DirectionalLightColour"));
   bg.addChild(dirLight);
   // Add point light sources for all that have a colour defined in
   // the constants file
   for(int i = 1; ; i++) {
     String l = new String("PointLight" + i);
     if(constants.getProperty(l + "ColourR") == null)
       break;
     System.err.println("Adding Point Light " + i);
     PointLight pl = new PointLight(
       constants.getColor3f(l + "Colour"),
       new Point3f(constants.getVector3f(l + "Position")),
       new Point3f(constants.getFloatValue(l + "AttenuationConstant"),
             constants.getFloatValue(l + "AttenuationLinear"),
             constants.getFloatValue(l + "AttenuationQuadratic")));
     pl.setInfluencingBounds(bounds);
     bg.addChild(pl);
   }
 }
 private void addMouseRotators(Bounds bounds, TransformGroup transformGroup) {
   MouseRotate myMouseRotate = new MouseRotate();
   myMouseRotate.setTransformGroup(transformGroup);
   myMouseRotate.setSchedulingBounds(bounds);
   bg.addChild(myMouseRotate);
   mouseTranslate = new MouseTranslate();
   mouseTranslate.setTransformGroup(transformGroup);
   mouseTranslate.setSchedulingBounds(bounds);
   bg.addChild(mouseTranslate);
   MouseZoom myMouseZoom = new MouseZoom();
   myMouseZoom.setTransformGroup(transformGroup);
   myMouseZoom.setSchedulingBounds(bounds);
   bg.addChild(myMouseZoom);
 }
 MouseTranslate mouseTranslate;
 public MouseTranslate getMouseTranslate() {
   return mouseTranslate;
 }
 public void addGraphElementView(GraphElementView view) {
   view.getBranchGroup().compile();
   transformGroup.addChild(view.getBranchGroup());
 }
 public TransformGroup getTransformGroup() {
   return transformGroup;
 }
  public void behaviorWakeup() {
  }

 public BranchGroup getBranchGroup() {
   return bg;
 }
  public void reorient() {
    javax.media.j3d.Transform3D rotate = new javax.media.j3d.Transform3D();
    transformGroup.setTransform(rotate);
  }
  public void setBackgroundColor(Color3f c) {
    background.setColor(c);
  }
  public float getFogDensity() {
    return fog.getDensity();
  }
  public void setFogDensity(float d) {
    fog.setDensity(d);
  }
  public void setRootPickingClient(PickingClient client) {
    pb.setRootPickingClient(client);
  }
  public void setPickingEnabled(boolean enabled) {
    pb.setEnable(enabled);
  }
}
