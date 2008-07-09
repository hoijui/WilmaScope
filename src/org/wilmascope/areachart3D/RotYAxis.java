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
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import org.wilmascope.areaChart3D.ValueLabel;
import org.wilmascope.areaChart3D.ValueLine;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
/** Class RotYAxis enables using mouse to rotate the scene graph with the Y axis
 */
public class RotYAxis extends MouseBehavior{
	protected MouseEvent mevent;
	private ValueLine lines;
	private Transform3D trans=new Transform3D();
	private ValueLabel labels;
	private Chart chart;
	// the normals the front,back, left,right faces of the chart
	private Vector3f[] normals={new Vector3f(1,0,0),
        	             new Vector3f(0,0,1),
		                 new Vector3f(-1,0,0),
		                 new Vector3f(0,0,-1),
		                 };
	private Vector3f[] positions=new Vector3f[4];	                 
	 public RotYAxis(Chart chart)
	 {
	 	super(chart.getRotTG());
	 	this.lines=chart.getValueLine();
	 	this.labels=chart.getValueLabels();
	 	this.chart=chart;
	 	transformGroup.getTransform(trans);
	 	//There are 4 possible places of adding the value label:4 corners of the chart
	 	for(int i=0;i<4;i++)
	 	  	trans.transform(normals[i]);
	 	positions[0]=new Vector3f(0,0,0);
	 	positions[1]=new Vector3f(0,0,-chart.getChartLength());
	 	positions[2]=new Vector3f(chart.getChartWidth(),0,-chart.getChartLength());
	 	positions[3]=new Vector3f(chart.getChartWidth(),0,0);  	
	 	  	 	  
	 }
	 public void initialize() {
        mouseEvents = new WakeupCriterion[3];
        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
        mouseCriterion = new WakeupOr(mouseEvents);
        wakeupOn (mouseCriterion);
        
        }
	public void processStimulus(Enumeration criteria){
	  WakeupCriterion wakeup;
      AWTEvent[] event;
      int id,buttonMask;
      double dx;
      double angleY;
      Transform3D temp=new Transform3D();
     
      while(criteria.hasMoreElements()){ 
          wakeup =(WakeupCriterion) criteria.nextElement();
          event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
       	  for(int i=0; i<event.length; i++){
          	 id = event[i].getID();
          	 mevent=(MouseEvent)event[i];
          	 buttonMask=((MouseEvent)event[i]).getModifiers();
          	 x =  mevent.getX();
             y =  mevent.getY();
            if ((id==MouseEvent.MOUSE_PRESSED)||(id==MouseEvent.MOUSE_CLICKED)){
               	       x_last=x;
                       y_last=y;
                   }
             if((buttonMask==MouseEvent.BUTTON1_MASK)&&(id==MouseEvent.MOUSE_DRAGGED)){
               //rotate with  y axis
               //move x direction to rotate around the y axis
                  dx = x- x_last;
                  angleY=dx*0.03;
                  x_last=x;
                  y_last=y;
                  temp.rotY(angleY);
                  transformGroup.getTransform(trans);
                   Matrix4f mat = new Matrix4f();
                   trans.get(mat);
                     
                   trans.setTranslation(new Vector3f(0,0,0));
                   trans.mul(temp,trans);
                   Vector3f translation = new Vector3f(mat.m03, mat.m13, mat.m23);
			       
		          trans.setTranslation(translation);
                  transformGroup.setTransform(trans);
                  lines.removeAllGeometries();
                 // transform the normals each time user rotate the scene graph
                 //if newNormal.z<0, it means that the correstponding face faces to the back
                 //and the lines on that face should not be shown
                  for(int j=0;j<=3;j++)
                  {
                  	Vector3f newNormal=new Vector3f(normals[j]);
                  	trans.transform(newNormal);
                  	if(newNormal.z>=0f){
                  	     lines.addGeometry(lines.getLineArrays()[j]);
                  	     //add the valueLabels to the face whose normal.x>=0
                  	     if(newNormal.x>=0){
                  	     	//translate the label to the right place
                  	         Transform3D transLabel=new Transform3D();
                  	         transLabel.setTranslation(positions[j]);
                  	         labels.setTransform(transLabel);
                    	    }
                       }
                  	
                  }
               
               }//if
           }//while
           wakeupOn(mouseCriterion);
       }     
   }

}