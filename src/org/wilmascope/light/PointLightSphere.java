package org.wilmascope.light;


import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import java.awt.*;
import java.util.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.PickTool;
/**
 * @author star
 *
 */
/** 
 * This class creates a sphere to represent the PointLight's position and colour
 */ 

public class PointLightSphere extends TransformGroup{
	private Color3f eColor    = new Color3f(0.0f, 1.0f, 0.0f);
    private Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
    private Color3f objColor  = new Color3f(0.0f, 0.0f,0.0f);
    private Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
    private Appearance a = new Appearance();
    
    private Transform3D translate=new Transform3D();
    private Sphere sphere;
    public PointLightSphere()
    {
    
      m.setCapability(Material.ALLOW_COMPONENT_READ);
  	  m.setCapability(Material.ALLOW_COMPONENT_WRITE);
	  m.setLightingEnable(true);
	  a.setMaterial(m);
	  a.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
	  a.setCapability(Appearance.ALLOW_MATERIAL_READ);
	  sphere=new Sphere(0.02f,a);
      this.addChild(sphere);
      this.setTransform(translate);
	  this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }
   /** Sets the position of the sphere 
    *  
    */ 
    public void setPosition(Point3f position)
	{
		translate.setTranslation(new Vector3f(position.x,position.y,position.z));
	    this.setTransform(translate);
	}
	/** Sets the color of the sphere
	 */
	public void setColor(Color3f color)
	{
		m.setEmissiveColor(color);
		a.setMaterial(m);
	}
	
 
}
