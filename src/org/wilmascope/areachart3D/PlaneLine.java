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
package org.wilmascope.areachart3D;

/**
 * @author Christine
 *
 */

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
/**Class PlaneLine creates the marking lines on the transparent plane 
 */
public class PlaneLine extends Shape3D{
	private Color3f color=new Color3f(0,0,0);
	public PlaneLine(int lineNum,float length,float height)
	{
		this.setGeometry(lineGeometry(lineNum,length,height));
		this.setAppearance(lineAppearance());
	}
	 private Geometry lineGeometry(int lineNum,float length,float height)
	   {
	    float deltaY=height/lineNum;
	    float y=deltaY;
	    Point3f[] coord=new Point3f[2*lineNum];
	    Color3f[] colors=new Color3f[2*lineNum];
	       for(int i=0;i<2*lineNum;i=i+2){
	           coord[i]=new Point3f(0,y,0);
	           coord[i+1]=new Point3f(0,y,-length);
	           y+=deltaY;
	           colors[i]=color;
	           colors[i+1]=color;
	         }
	   LineArray lines=new LineArray(2*lineNum,LineArray.COORDINATES|LineArray.COLOR_3);     
	   lines.setCoordinates(0,coord);
	   lines.setColors(0,colors);
	   return lines;
	   }
	 private Appearance lineAppearance() {
          Appearance materialAppear = new Appearance();
	    Material material = new Material();   
        LineAttributes lineAttrib = new LineAttributes();
        lineAttrib.setLineWidth(0.1f);
        materialAppear.setLineAttributes(lineAttrib);
    
       materialAppear.setMaterial(material);
        return materialAppear;

        }    
}
