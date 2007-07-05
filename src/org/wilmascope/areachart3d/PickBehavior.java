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

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.Node;
import javax.media.j3d.PickRay;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class PickBehavior enables using mouse to select differect sectors in the scene graph
 *
 */
public class PickBehavior extends Behavior {

   WakeupCriterion[] mouseEvents;
   WakeupOr mouseCriterion;
   int x, y;
   int x_last, y_last;
   double x_factor, y_factor;
   TransformGroup transformGroup;
   BranchGroup branchGroup;
   Canvas3D canvas3D;
     PickRay pickRay = new PickRay();
   SceneGraphPath sceneGraphPath[];
   Appearance highlight;
   boolean parallel;
   Chart chart;
   public PickBehavior(  BranchGroup branchGroup, Canvas3D canvas3D,Bounds bounds,Chart chart) {

      this.canvas3D = canvas3D;
      this.branchGroup = branchGroup;
      this.setSchedulingBounds(bounds);
      this.chart=chart;
      
   }

   public void initialize() {
      x = 0;
      y = 0;
      x_last = 0;
      y_last = 0;
      x_factor = .02;
      y_factor = .02;

      mouseEvents = new WakeupCriterion[1];
     
      mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
      mouseCriterion = new WakeupOr(mouseEvents);
      wakeupOn (mouseCriterion);
   }

   public void processStimulus (Enumeration criteria) {
      WakeupCriterion wakeup;
      AWTEvent[] event;
      int id;
      int dx, dy;

      while (criteria.hasMoreElements()) {
         wakeup = (WakeupCriterion) criteria.nextElement();
         if (wakeup instanceof WakeupOnAWTEvent) {
            event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
            for (int i=0; i<event.length; i++) { 
               id = event[i].getID();
                if (id == MouseEvent.MOUSE_PRESSED) {

                  x = x_last = ((MouseEvent)event[i]).getX();
                  y = y_last = ((MouseEvent)event[i]).getY();

                  Point3d eyePos = new Point3d();
                  canvas3D.getCenterEyeInImagePlate(eyePos);

                  Point3d mousePos = new Point3d();
                  canvas3D.getPixelLocationInImagePlate(x, y, mousePos);

                  Transform3D transform3D = new Transform3D();
                  canvas3D.getImagePlateToVworld(transform3D);

                  transform3D.transform(eyePos);
                  transform3D.transform(mousePos);

                  Vector3d mouseVec;
                 
                 mouseVec = new Vector3d();
                 mouseVec.sub(mousePos, eyePos);
                 mouseVec.normalize();
                              
                  pickRay.set(mousePos, mouseVec);
                  sceneGraphPath = branchGroup.pickAllSorted(pickRay);
 
                  if (sceneGraphPath != null) {
                     for (int j=0; j<sceneGraphPath.length; j++) {
                        if (sceneGraphPath[j] != null) {
                           Node node = sceneGraphPath[j].getObject();
                           if (node instanceof Shape3D) {
                              try {
                                 Object pickElement=node.getUserData();
                                 if((pickElement!=null)&&(pickElement instanceof ChartAreaShape))
                                        {
                                         chart.setSectorPicked((ChartAreaShape)pickElement);
                                         chart.showFigure();
                                         break;
                                         }
                                 }
                              catch (CapabilityNotSetException e) {
                                 // Catch all CapabilityNotSet exceptions and
                                 // throw them away, prevents renderer from
                                 // locking up when encountering "non-selectable"
                                 // objects.
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
     
      wakeupOn (mouseCriterion);
   }
   }
}
