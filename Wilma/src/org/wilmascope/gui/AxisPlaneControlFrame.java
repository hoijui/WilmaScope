package org.wilmascope.gui;

import javax.swing.*;
import java.awt.*;
import org.wilmascope.control.GraphControl;
import org.wilmascope.view.GraphCanvas;
import javax.vecmath.*;
import javax.media.j3d.*;
import java.awt.event.*;

import com.sun.j3d.utils.picking.PickTool;
/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */

public class AxisPlaneControlFrame extends JFrame {
  GridLayout gridLayout1 = new GridLayout();

  public AxisPlaneControlFrame() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public AxisPlaneControlFrame(GraphControl gc) {
    this();
    canvas = gc.getGraphCanvas();
    root = gc.getRootCluster();
    org.wilmascope.graph.NodeList l = root.getCluster().getAllNodes();
    Point3f c = l.getBarycenter();
    float width = l.getWidth();
    float height = l.getHeight();
    System.out.println("width="+width+",height="+height);
    axisPlaneBG = new BranchGroup();
    axisPlaneBG.setCapability(BranchGroup.ALLOW_DETACH);
    axisPlaneTG = new TransformGroup();
    axisPlaneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    Appearance ap = new Appearance();
    TransparencyAttributes transparencyAttributes
     = new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.6f);
    Material m = org.wilmascope.view.Colours.greyBlueMaterial;
    ap.setMaterial(m);
    ap.setTransparencyAttributes(transparencyAttributes);
    com.sun.j3d.utils.geometry.Box plane = new com.sun.j3d.utils.geometry.Box(width/1.9f,height/1.9f,0.01f,ap);
    for(int i=0;i<6;i++) {
      Shape3D shape = plane.getShape(i);
      shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
      try {
        PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);
      } catch(javax.media.j3d.RestrictedAccessException e) {
        //System.out.println("Not setting bits on already setup shared geometry");
      }
    }
    axisPlaneTG.addChild(plane);
    axisPlaneBG.addChild(axisPlaneTG);
    axisPlaneBG.compile();
    showAxisPlane();

    centroid = new Vector3f(c);
    Transform3D trans = new Transform3D();
    trans.setTranslation(centroid);
    axisPlaneTG.setTransform(trans);
  }
  private void jbInit() throws Exception {
    box1 = Box.createHorizontalBox();
    box2 = Box.createVerticalBox();
    box3 = Box.createVerticalBox();
    this.getContentPane().setLayout(gridLayout1);
    upButton.setText("Up");
    upButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        upButton_actionPerformed(e);
      }
    });
    downButton.setText("Down");
    downButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        downButton_actionPerformed(e);
      }
    });
    showButton.setText("Show");
    showButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showButton_actionPerformed(e);
      }
    });
    hideButton.setText("Hide");
    hideButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(box1, null);
    box1.add(box2, null);
    box2.add(upButton, null);
    box2.add(downButton, null);
    box1.add(box3, null);
    box3.add(showButton, null);
    box3.add(hideButton, null);
  }
  GraphCanvas canvas;
  GraphControl.ClusterFacade root;
  TransformGroup axisPlaneTG;
  BranchGroup axisPlaneBG;
  javax.swing.Box box1;
  javax.swing.Box box2;
  JButton upButton = new JButton();
  JButton downButton = new JButton();
  Box box3;
  JButton showButton = new JButton();
  JButton hideButton = new JButton();

  void upButton_actionPerformed(ActionEvent e) {
    Transform3D trans = new Transform3D();
    centroid.z+=0.2f;
    trans.setTranslation(centroid);
    axisPlaneTG.setTransform(trans);
  }
  Vector3f centroid;

  void downButton_actionPerformed(ActionEvent e) {
    Transform3D trans = new Transform3D();
    centroid.z-=0.2f;
    trans.setTranslation(centroid);
    axisPlaneTG.setTransform(trans);
  }

  void hideButton_actionPerformed(ActionEvent e) {
    axisPlaneBG.detach();
    hideButton.setEnabled(false);
    showButton.setEnabled(true);
  }

  void showButton_actionPerformed(ActionEvent e) {
    showAxisPlane();
  }
  void showAxisPlane() {
    canvas.getTransformGroup().addChild(axisPlaneBG);
    showButton.setEnabled(false);
    hideButton.setEnabled(true);
  }
}