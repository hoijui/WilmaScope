/**
 * @author Christine
 *
 */
package org.wilmascope.light;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
/** This class enables using mouse to adjust the position, direction, spreadangle of the spotlight
 */
public class SpotLightMouseCtrl extends MouseBehavior{
    
	private Vector3f direction=new Vector3f();
	private Transform3D temp1=new Transform3D();
	private Transform3D temp2=new Transform3D();
	//for the cone translate
	private Matrix4d transMatrix=new Matrix4d();
	private Transform3D oldMatrix=new Transform3D();
	private Point3f position=new Point3f();
	private Color3f color=new Color3f();
	float spreadAngle;	

	//for the pick and mouse event
	protected MouseEvent mevent;
	private boolean buttonPressed;
	
	/**{@link SpotLightPanel}
	 */
    private SpotLightPanel spotPane;
    /**{@link SpotLightCone}
	 */
    private SpotLightCone cone;
    /**{@link SpotLight}
	 */
    private  SpotLight spotLight;
	public SpotLightMouseCtrl(SpotLightCone c,SpotLight l,SpotLightPanel spotPane)
	{
		
	    super(c);
	    cone=c;
	    spotLight=l;   
	    this.spotPane=spotPane; 
	    spotLight.getDirection(direction);
    	spotLight.getPosition(position);
    	cone.setDirection(direction);
        cone.setPosition(position);
        spotLight.getColor(color);
        cone.setColor(color);
        spreadAngle=spotLight.getSpreadAngle();
        cone.setSpreadAngle(spreadAngle);
	}
	/**  Initializes the wake up event
	 */
	public void initialize() {
    mouseEvents = new WakeupCriterion[3];
    mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
    mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
    mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
    mouseCriterion = new WakeupOr(mouseEvents);
    wakeupOn (mouseCriterion);
   
  }
/**Processes the mouse event,making the spot light and corrisponding cone changing with the
 * movement of the mouse
 */
    
public void processStimulus(Enumeration criteria){
	  WakeupCriterion wakeup;
      AWTEvent[] event;
      int id,buttonMask;
      double dx=0, dy=0,dz=0;
      double angleX,angleY;
      boolean shiftDown;
      boolean altDown;
      double dAngle;//  spreadAngle
      while(criteria.hasMoreElements()){ 
          wakeup =(WakeupCriterion) criteria.nextElement();
          event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
          
       	  for(int i=0; i<event.length; i++){
          	 id = event[i].getID();
          	 buttonMask=((MouseEvent)event[i]).getModifiers();
          	 x = ((MouseEvent)event[i]).getX();
             y = ((MouseEvent)event[i]).getY();
             shiftDown=((MouseEvent)event[i]).isShiftDown();
             altDown=((MouseEvent)event[i]).isAltDown();
               if ((id==MouseEvent.MOUSE_PRESSED)||(id==MouseEvent.MOUSE_CLICKED)){
               	    x_last=x;
                    y_last=y;
                    
                   }
                   
            if((buttonMask==MouseEvent.BUTTON1_MASK)&&(id==MouseEvent.MOUSE_DRAGGED)){
                  //move x direction to rotate around the y axis                
                  dx = x- x_last;
                  angleY=dx*0.03;
                  //move y direction to rotate around the x axis
                  dy = -(y- y_last);
                  angleX=dy*0.03;
                  x_last=x;
                  y_last=y;
                  temp1.rotY(angleY);
                  temp2.rotX(angleX);
                  temp2.mul(temp1);
                  temp2.transform(direction);
                  spotLight.setDirection(direction);
                  cone.setDirection(direction);
                  spotPane.xDir.setText(""+direction.x);
                  spotPane.yDir.setText(""+direction.y);
                  spotPane.zDir.setText(""+direction.z);
                                                     
               }
             if((buttonMask==MouseEvent.BUTTON3_MASK)&&(id==MouseEvent.MOUSE_DRAGGED)){
                // right button to change the x,y position of the spotlight  
                dx = x- x_last;
                dy = -(y- y_last);
                dx=(float) dx*0.005;
                dy=(float) dy*0.005;
                x_last=x;
                y_last=y; 
                if((Math.abs(dx)>0.5)||(Math.abs(dy)>0.5))
                      continue; 
                                 
                  cone.getTransform(oldMatrix);
                  oldMatrix.get(transMatrix);
                  transMatrix.m03+=dx;
                  transMatrix.m13+=dy;
                  oldMatrix.set(transMatrix);
                  cone.setTransform(oldMatrix);
                  
                  position.x+=dx;
                  position.y+=dy;
                  spotLight.setPosition(position);
                 
                                                   
              } 
              
              if(shiftDown&&(id==MouseEvent.MOUSE_DRAGGED)){
              	  //shiftdown + mouse dragged to  zoom
                   dx = x- x_last;
                   dz=dx*0.005;
                    x_last=x;
                    y_last=y;  
                  if(Math.abs(dz)>0.5)
                      continue;       
                  cone.getTransform(oldMatrix);
                  oldMatrix.get(transMatrix);
                  transMatrix.m23+=dz;
                  oldMatrix.set(transMatrix);
                  cone.setTransform(oldMatrix);
                  
                  position.z+=dz;
                  spotLight.setPosition(position);
                  
                  spotPane.xPos.setText(""+position.x);
                  spotPane.yPos.setText(""+position.y);
                  spotPane.zPos.setText(""+position.z);
            
                    
                  }  
                 //alt down + mouse dragged to adjust the spread angle 
              if(altDown&&(id==MouseEvent.MOUSE_DRAGGED)&&(cone!=null)){
                   dx = x- x_last;
                   dAngle=(float)dx*0.005;
                    x_last=x;
                    y_last=y; 
                   spreadAngle=spotLight.getSpreadAngle();
                   spreadAngle+=dAngle;
                   //the biggest spread Angle is PI/2
                   if(spreadAngle>Math.PI/2) spreadAngle=(float)Math.PI/2;
                   if(spreadAngle<0) spreadAngle=0; 
                   //the smallest spread Angle is 0
                   spotLight.setSpreadAngle((float)spreadAngle);
                   cone.setSpreadAngle((float)spreadAngle);
                   spotPane.SpreadAngle.setText(""+spreadAngle);
                   
              }  
             
            }
           wakeupOn(mouseCriterion);
       }     
   }
   void refresh()
   {
   	x=y=0;
   	x_last=y_last=0;
   }
  
}
