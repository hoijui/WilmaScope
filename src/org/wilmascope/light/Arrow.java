package org.wilmascope.light;

/**
 * @author Christine
 *
 */
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import java.awt.*;
import java.util.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.PickTool;

/** This class creates an arrow to represent the directional light's colour and direction
 */
public class Arrow extends TransformGroup{
  private  Cone cone;
  private  Cylinder cylinder;
 
  private Vector3f xAxis=new Vector3f(1,0,0);
  private Vector3f yAxis=new Vector3f(0,1,0);
  private Color3f eColor    = new Color3f(1.0f, 0.0f, 0.0f);
  private Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
  private Color3f objColor  = new Color3f(0.0f, 0.0f,0.0f);
  private Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
  private Appearance a = new Appearance();
  private Transform3D translate=new Transform3D();
  private Transform3D tempZ=new Transform3D(); 
  private Transform3D tempY=new Transform3D(); 
  private TransformGroup ZGroup=new TransformGroup();
  private TransformGroup YGroup=new TransformGroup();
  
   public Arrow()
  {
  	 //material of the arrow
  	 m.setCapability(Material.ALLOW_COMPONENT_READ);
  	 m.setCapability(Material.ALLOW_COMPONENT_WRITE);
	 m.setLightingEnable(true);
	 a.setMaterial(m);
	 a.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
	 a.setCapability(Appearance.ALLOW_MATERIAL_READ);
	cone=new Cone(0.03f, 0.06f,a);
	cone.setCapability(Geometry.ALLOW_INTERSECT);
	Transform3D t=new Transform3D();
  	t.setTranslation(new Vector3f(0f,0.07f,0.0f));
  	TransformGroup transCone=new TransformGroup(t);
  	transCone.addChild(cone);
  	
  	cylinder=new Cylinder(0.02f, 0.08f,a);
  	ZGroup.addChild(transCone); 	
    ZGroup.addChild(cylinder);
    
    YGroup.addChild(ZGroup);   
    this.addChild(YGroup);
    YGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    YGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    ZGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    ZGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	translate.setTranslation(new Vector3f(0f,0f,0.6f));
	this.setTransform(translate);
  }
/** Sets the color of the arrow 
 */  
   	public void setColor(Color3f color)
	{
		m.setEmissiveColor(color);
		a.setMaterial(m);
	}
/**  
 * Rotates the arrow to the specified direction.
 */
/*
 * When the arrow is first created, it points to the direction (0,1,0). 
 * Two angle was calculated:
 *     FI--angle between the y axis and the direction vector
 *     Theta--angle between x axis and (direction.x,0,direction.z). 
 * Then the arrow was first rotate around the z axis and then the y around to the right
 * direction.
 */   
public void setDirection(Vector3f direction)
     {
     	
   	 //angle between the y axis and the direction vector
   	 float FI;
   	 //angle between the x axis and the vector
   	 float Theta;
   	 FI=-direction.angle(yAxis);
     Theta=-xAxis.angle(new Vector3f(direction.x,0,direction.z)); 
   	 if(direction.z<0) Theta=-Theta;
   	  
   	 tempZ.rotZ(FI);
   	 ZGroup.setTransform(tempZ);
   	 tempY.rotY(Theta);
   	 YGroup.setTransform(tempY);
  
     }
 }
