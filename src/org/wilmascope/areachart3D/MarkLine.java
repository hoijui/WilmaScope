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
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
/** Class MarkLine create the marking lines for the calendar.
 */
public class MarkLine extends Shape3D{
	private Color3f color=new Color3f(1,1,1);
	 private LineArray lines;
/**@param lineNum The size of the calendar vector
 * @param length The length of the chart
 * @param width The width of the chart
 * @param lineLength the length of the marking line
 */
    public MarkLine(int lineNum,float length,float width,float lineLength){
        this.setGeometry(lineGeometry(lineNum,length,width,lineLength));
        this.setAppearance(lineAppearance());
    }

    private Geometry lineGeometry(int lineNum,float length,float width,float lineLength){
    	float  deltaX=width/(lineNum-1);
    	Point3f[] coords=new Point3f[4*lineNum];
    	Color3f[] colors=new Color3f[4*lineNum];
    	float x=0;
    	
    	for(int i=0;i<2*lineNum;i+=2){
    	   coords[i]=new Point3f(x,0,lineLength);
    	   coords[i+1]=new Point3f(x,0,0);
    	   coords[2*lineNum+i]=new Point3f(x,0,-length);
    	   coords[2*lineNum+i+1]=new Point3f(x,0,-length-lineLength);
    	   x+=deltaX;
    	}
    	for(int j=0;j<4*lineNum;j++){
    	  colors[j]=color;
    	}
    	
       lines=new LineArray(4*lineNum,LineArray.COORDINATES|LineArray.COLOR_3);     
	   lines.setCoordinates(0,coords);
	   lines.setColors(0,colors);  
       return lines;
    }
      private Appearance lineAppearance() {
          Appearance materialAppear = new Appearance();
	    Material material = new Material();   
        LineAttributes lineAttrib = new LineAttributes();
        lineAttrib.setLineWidth(0.05f);
        materialAppear.setLineAttributes(lineAttrib);
       materialAppear.setMaterial(material);
        return materialAppear;

        } 
}
