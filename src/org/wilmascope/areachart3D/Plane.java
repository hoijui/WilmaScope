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
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.wilmascope.areaChart3D.PlaneLine;
import org.wilmascope.areaChart3D.TransparentPlain;
/**Class Plane creates a transparent plane
 */
public class Plane extends TransformGroup{
	private TransparentPlain plain;
	private PlaneLine linesFront;
	private PlaneLine linesBack;
	private float xOffset;
	public Plane(int lineNum,float length,float height){
	   plain=new TransparentPlain(length,height);
       linesFront=new PlaneLine(lineNum,length,height);
       linesBack=new PlaneLine(lineNum,length,height);
       //move linesFront a little before the plain
       TransformGroup TG1=new TransformGroup();
       Transform3D temp=new Transform3D();
       temp.setTranslation(new Vector3f(0.001f,0,0));
       TG1.setTransform(temp);
       TG1.addChild(linesFront);
       //mobe the linesBack a little behind the plain
       TransformGroup TG2=new TransformGroup();
       temp=new Transform3D();
       temp.setTranslation(new Vector3f(-0.001f,0,0));
       TG2.setTransform(temp);
       TG2.addChild(linesBack);
       this.addChild(plain);
       this.addChild(TG1);
       this.addChild(TG2);
       this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); 
       this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); 
       
	}
/** Sets the position of the plane
 */	
	public void setPlanePosition(float xOffset)
	{
		Transform3D trans=new Transform3D();
       trans.setTranslation(new Vector3f(xOffset,0,0));
       this.setTransform(trans);
       this.xOffset=xOffset;
	}
/**@return The position of the plane
 */	
	public float getPlanePosition()
	{
		return xOffset;
	}

}
