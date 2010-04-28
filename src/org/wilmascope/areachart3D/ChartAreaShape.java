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

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.PickTool;
/**
 * @author Christine
 *Class ChartAreaShape creates the geometry of each sector
 */
public class ChartAreaShape extends Primitive{
	 private float thick;
	 private float width;
	 private float[] yCoord;
	 private Color3f color;
	 private Appearance materialAppear = new Appearance();
	 private  Material material = new Material();
	 private float[] values;    
	 private float zCoord;
	 private String sectorName;
/**@param values Array containing the data of the sector
 * @param width The width of the chart
 * @param thick Thick of the 3D shape
 * @param max The biggest value in the Chart
 * @param color The color of the sector
 * @param sectorName Name of the sector
 */	 
     public ChartAreaShape(float[] values,float width,float thick,float max,Color3f color,float zCoord,String sectorName)
     {
     	this.width=width;
     	this.thick=thick;
        this.color=color;
        this.values=values;	
        this.zCoord=zCoord;
        this.sectorName=sectorName;  	
        yCoord=new float[values.length+2];
        yCoord[0]=yCoord[values.length+1]=0f; 
     	for(int i=1;i<=values.length;i++)
         	  yCoord[i]=(float)values[i-1]/max;
        //each 
        Shape3D front=plateGeometry(0,true);
        front.setUserData(this);
       	this.addChild(front);
     	
     	Shape3D back=plateGeometry(-thick,false);
     	back.setUserData(this);
     	this.addChild(back);
     	
     	Shape3D side=sideGeometry(0);
     	side.setUserData(this);
     	this.addChild(side);
     	
     	this.setAppearance(sectorAppearance());
     	this.setCapability(Shape3D.ENABLE_PICK_REPORTING);
      
     }
       private Shape3D plateGeometry(float zCoord,boolean back) {
       	Point3f[] coord=new Point3f[yCoord.length];
     	float widthX=width/(yCoord.length-3);
     	float xCoord=0f;
        coord[0]=new Point3f(xCoord,yCoord[0],zCoord);
        for(int i=1;i<yCoord.length-1;i++){
     		 coord[i]=new Point3f(xCoord,yCoord[i],zCoord);
     		 xCoord+=widthX;
     	     }
        coord[yCoord.length-1]=new Point3f(xCoord-widthX,yCoord[yCoord.length-1],zCoord);
        
    
     	GeometryInfo g = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
     	g.setCoordinates(coord);
        Color3f[] colors=new Color3f[yCoord.length];
        for(int i=0;i<coord.length;i++)
		     colors[i]=color;
        g.setColors(colors); 
       
     	int[] stripCounts={yCoord.length};
        g.setStripCounts(stripCounts);       
       
       NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(g);
        if(back){
        	Vector3f[] normals=g.getNormals();
        	for(int i=0;i<normals.length;i++){
        	  	normals[i].x=-normals[i].x; 
        	   normals[i].y=-normals[i].y; 
        	   normals[i].z=-normals[i].z; 
        	}
        	g.setNormals(normals);
        }
        g.recomputeIndices();

        Stripifier st = new Stripifier();
        st.stripify(g);
        g.recomputeIndices();
      
        Shape3D shape=new Shape3D();
         shape.setGeometry(g.getGeometryArray());
        PickTool.setCapabilities(shape,PickTool.INTERSECT_FULL);
       
        return shape;		
        
       }
       private Shape3D sideGeometry(float zCoord)
       {
       	Point3f[] coord=new Point3f[4*(yCoord.length-1)];
     	Vector3f[] normals;
     		
     	float widthX=width/(yCoord.length-3);
     	float xCoord=0f;
     	coord[0]=new Point3f(xCoord,yCoord[0],zCoord);
     	coord[1]=new Point3f(xCoord,yCoord[1],zCoord);
     	coord[2]=new Point3f(xCoord,yCoord[1],zCoord-thick);
     	coord[3]=new Point3f(xCoord,yCoord[0],zCoord-thick);
     	for(int i=1;i<yCoord.length-2;i++)
     	{
     	coord[4*i]=new Point3f(xCoord,yCoord[i],zCoord);
     	coord[4*i+1]=new Point3f(xCoord+widthX,yCoord[i+1],zCoord);
     	coord[4*i+2]=new Point3f(xCoord+widthX,yCoord[i+1],zCoord-thick);
     	coord[4*i+3]=new Point3f(xCoord,yCoord[i],zCoord-thick);
     		
     		xCoord+=widthX;
     	}
        coord[coord.length-4]=new Point3f(xCoord,yCoord[yCoord.length-2],zCoord);
     	coord[coord.length-3]=new Point3f(xCoord,yCoord[yCoord.length-1],zCoord);
     	coord[coord.length-2]=new Point3f(xCoord,yCoord[yCoord.length-1],zCoord-thick);
     	coord[coord.length-1]=new Point3f(xCoord,yCoord[yCoord.length-2],zCoord-thick);
     	GeometryInfo g = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
     	g.setCoordinates(coord);
      
		Color3f[] colors=new Color3f[4*(yCoord.length-1)];
        for(int i=0;i<4*(yCoord.length-1);i++)
		     colors[i]=color;
        g.setColors(colors); 
        
     	int[] stripCounts=new int[yCoord.length-1];
     	for(int i=0;i<stripCounts.length;i++)
     	    stripCounts[i]=4;
        g.setStripCounts(stripCounts);       
       NormalGenerator ng = new NormalGenerator();
       ng.generateNormals(g);
       g.getGeometryArray().setCapability(GeometryArray.ALLOW_COORDINATE_READ);
     	
     	Shape3D shape=new Shape3D();
     	 shape.setGeometry(g.getGeometryArray());
     	PickTool.setCapabilities(shape,PickTool.INTERSECT_FULL);
     	
       
        return shape;			
       }
     
      private Appearance sectorAppearance() {
           
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        materialAppear.setPolygonAttributes(polyAttrib);
        materialAppear.setMaterial(material);
        material.setDiffuseColor(color);
     	material.setSpecularColor(color);
        return materialAppear;

        } 
     public void setAppearance(Appearance ap){
      ((Shape3D)getChild(0)).setAppearance(ap);
      ((Shape3D)getChild(1)).setAppearance(ap);
      ((Shape3D)getChild(2)).setAppearance(ap);
      
       }
  /** @return The array containing the XCategories's data
   */   
    
    public float getValue(int index)
    {
    	
    	return values[index];
    }
    
    public Shape3D getShape(int partId) {
    if ((partId >=0) && (partId <=2))
	return (Shape3D)getChild(partId);
    return null;
  }
   public Appearance getAppearance(int partId) {
	if (partId > 2 || partId < 0) return null;
	return getShape(partId).getAppearance();
    }
  /**Get the sector's position in Z axis
   */  
   public float getZCoord()
   {
   	return zCoord;
   }
   /**set the sector's position in Z axis
    */
   public void setZCoord(float zCoord)
   {
   	this.zCoord=zCoord;
   }
   public String getSectorName(){
     return sectorName;
   }
           
}
