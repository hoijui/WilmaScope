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
 * @author star
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
/**Class keyboardCtrl controls the movement of the transparent plane
 */
public class KeyboardCtrl extends Behavior{
    private WakeupCriterion w1 = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    private WakeupCriterion w2 = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    private WakeupCriterion[] warray = { w1};
    private WakeupCondition w = new WakeupOr(warray);
    private KeyEvent eventKey;
    private Plane plane;
    private float deltaX;
    private int step;
    private int dateNum;
    private Chart chart;
    public KeyboardCtrl(Chart chart)
    {
           this.plane=chart.getPlane();
           this.dateNum=chart.getCalendar().size();
           deltaX=chart.getChartWidth()/(dateNum-1);        	
           step=0;
           this.chart=chart;                
    }
    public void initialize() {
	// Establish initial wakeup criteria
	    w = new WakeupOr(warray);
	    wakeupOn(w);
	   	
    } 
    public void processStimulus(Enumeration criteria) {
	WakeupOnAWTEvent ev;
	WakeupCriterion genericEvt;
	AWTEvent[] events;
	boolean sawFrame = false;
      
	while (criteria.hasMoreElements()){
		
	    genericEvt = (WakeupCriterion) criteria.nextElement();
	    if (genericEvt instanceof WakeupOnAWTEvent) {
		ev = (WakeupOnAWTEvent) genericEvt;
		events = ev.getAWTEvent();
		processAWTEvent(events);
	    } 
	   
	}	

	// Set wakeup criteria for next time
	wakeupOn(w);
   }     
   /**
     *  Process a keyboard event
     */
    private void processAWTEvent(AWTEvent[] events) {
	for (int loop = 0; loop < events.length; loop++) {
	    if (events[loop] instanceof KeyEvent) {
		eventKey = (KeyEvent) events[loop];
		if (eventKey.getID() == KeyEvent.KEY_PRESSED){
		         int keyCode =  eventKey.getKeyCode();
	             float xOffset=plane.getPlanePosition();
	            //move the plane back a little if <- is pressed
	             if((keyCode==KeyEvent.VK_LEFT)&&(step>=1)){
	             	          xOffset-=deltaX;
	                          step--;
	                          }
	             //move the plane backward if -> is pressed             
	             if((keyCode==KeyEvent.VK_RIGHT)&&(step<=dateNum-2)){              	           
	    		              xOffset+=deltaX;
	                          step++;
	                          }
	             //sort the sectors if enter is pressed             
	             if(keyCode==KeyEvent.VK_ENTER)	             	          	           
	    		             chart.sort();
	                          
	    		 plane.setPlanePosition(xOffset);
	    		 chart.showFigure();
		}
	    }
	}
    }

 
}
