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
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import org.wilmascope.areaChart3D.ChartAreaShape;
/**Clasee XCategories creates a 3D shape to represents different business sectors.
 * Together store the sector's name and position in z axis.
 */
public class XCategories extends BranchGroup{
	private ChartAreaShape shape;
	private String sectorName;
	private TransformGroup TG=new TransformGroup();
	private float zCoord;
/**@param values Array containing the data of the sector
 * @param width The width of the chart
 * @param thick Thick of the 3D shape
 * @param max The biggest value in the Chart
 * @param color The color of the sector
 * @param sectorName Name of the sector
 */	 	
	public XCategories(float[] values, 
	               float zCoord,float width,float thick,
	               float max,Color3f color,
	               String sectorName)
	{
		shape=new ChartAreaShape(values,width,0.05f,max,color,zCoord,sectorName);
	    Transform3D trans=new Transform3D();
	    trans.setTranslation(new Vector3f(0,0,zCoord));    
	    TG.setTransform(trans);
	    TG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    TG.addChild(shape);
	    this.zCoord=zCoord;
	    this.addChild(TG);	
		this.sectorName=sectorName;
		this.setCapability(BranchGroup.ALLOW_DETACH);
   }
	public ChartAreaShape getShape(){
		return shape;
	}
	public String getSectorName()
	{
		return sectorName;
	}
	public void setZCoord(float zCoord)
	{
		Transform3D trans=new Transform3D();
	    trans.setTranslation(new Vector3f(0,0,zCoord));    
	    TG.setTransform(trans);
	    shape.setZCoord(zCoord);
	}
	public float getZCoord()
	{
		return zCoord;
	}

}
