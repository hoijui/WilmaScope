/*
 * The following source code is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Christine Wu however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform me(sainttale@hotmail.com)
 * if you do anything really cool with it.
 *
 *
 * -- Christine, 2003
 */
package org.wilmascope.areaChart3D;

/**
 * @author Christine
 *
 */
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
/**Class TransparentPlain creates the Geometry of the moving plane
 */
public class TransparentPlain extends Shape3D{
	Color3f color=new Color3f((float)204/255,1f,1f);
    public TransparentPlain(float length,float height)
    {
        this.setGeometry(plainGeometry(length,height));
        this.setAppearance(plainAppearance());	
    } 
    private Geometry plainGeometry(float length,float height){
      Point3f[] coords={new Point3f(0,0,0),
      	                new Point3f(0,0,-length),
      	                new Point3f(0,height,-length),
      	                new Point3f(0,height,0)
                       }; 
      Color3f[] colors={color,color,color,color};
      QuadArray q=new QuadArray(4,QuadArray.COLOR_3|QuadArray.COORDINATES);    
      q.setColors(0,colors);
      q.setCoordinates(0,coords);  
      return q;      
    }
     
    private Appearance plainAppearance() {
    	TransparencyAttributes transparencyAttributes
            = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.2f);
          Appearance materialAppear = new Appearance();
	    Material material = new Material();   
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        materialAppear.setPolygonAttributes(polyAttrib);
       materialAppear.setMaterial(material);
       materialAppear.setTransparencyAttributes(transparencyAttributes);
        return materialAppear;

        } 
}
