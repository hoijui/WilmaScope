package org.wilmascope.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.wilmascope.columnlayout.ColumnLayout;
import org.wilmascope.control.GraphClient;
import org.wilmascope.control.GraphControl;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;
import org.wilmascope.view.GraphCanvas;

import com.sun.j3d.utils.picking.PickTool;
/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */

class TreePanel extends JPanel {
  ImageIcon tree=new ImageIcon(ClassLoader.getSystemResource("images/treeviewbuttons.png"));
  public TreePanel() {
    super();
    setPreferredSize(new Dimension(400,400));
  }
  public void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g;

    int w = getWidth();
    int h = getHeight();
/*
    if (bi == null || bi.getWidth() != w || bi.getHeight() != h) {
      bi = (BufferedImage) createImage(w, h);
      Graphics2D big = bi.createGraphics();
      big.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      //render(w, h, big);
    }
*/
    // Draws the buffered image to the screen.
    g2.drawImage(tree.getImage(), 0, 0, this);
  }
  void render(int w, int h, Graphics2D g) {
    for(int i = 0;i<buttons.length;i++) {
      JComponent c = buttons[i];
      int y = c.getLocation(null).y;
      System.out.println("blah"+y);
      g.drawRect(0,y,5,5);
    }
  }

  BufferedImage bi;
  JComponent buttons[];
}
public class SliceViewControlFrame extends JFrame {
  GraphControl gc;
  public SliceViewControlFrame(GraphControl gc) {
    this.gc = gc;
    gc.addGraphClient(new GraphClient(){
      public void balanced() {
        System.out.println("Resetting axis plane!");
        init();
      }
    });
    init();
  }
  void init() {
    canvas = gc.getGraphCanvas();
    root = gc.getRootCluster();
    getContentPane().removeAll();
    NodeList l = root.getCluster().getAllNodes();
    for(l.resetIterator();l.hasNext();) {
      Node n = l.nextNode();
      if(n instanceof Cluster) {
        if(((Cluster)n).getLayoutEngine() instanceof ColumnLayout) {
          ColumnLayout c = (ColumnLayout)((Cluster)n).getLayoutEngine();
          strataSeparation = c.getStrataSeparation();
          strataCount = c.getStrataCount();
          extraSpacing = c.getExtraSpacing();
          break;
        }
      }
    }
    bottomLeft = new Point3f();
    topRight = new Point3f();
    centroid = new Point3f();
    l.getBoundingBoxCorners(bottomLeft,topRight,centroid);
    float width = topRight.x - bottomLeft.x;
    float height = topRight.y - bottomLeft.y;
    System.out.println("width="+width+",height="+height);
    if(axisPlaneBG!=null) {
      axisPlaneBG.detach();
    }
    axisPlaneBG = new BranchGroup();
    axisPlaneBG.setCapability(BranchGroup.ALLOW_DETACH);
    axisPlaneTG = new TransformGroup();
    axisPlaneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    Appearance ap = new Appearance();
    TransparencyAttributes transparencyAttributes
     = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.6f);
    RenderingAttributes ra = new RenderingAttributes();
    ra.setDepthBufferEnable(true);
    ap.setRenderingAttributes(ra);
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

    Transform3D trans = new Transform3D();
    trans.setTranslation(new Vector3f(centroid));
    axisPlaneTG.setTransform(trans);
    
    r = root.getCluster();
    while(r.getNodes().size()==1) {
      r = (Cluster)r.getNodes().get(0);
    }
    drawingPanel = new DrawingPanel(r, bottomLeft, topRight);
    upButton = new JButton();
    downButton = new JButton();
    showButton = new JButton();
    hideButton = new JButton();
    printButton = new JButton();
    unionFrameButton = new JButton();
    setSelectedStratum(0);
    hBox = Box.createHorizontalBox();
    Box buttonBox = Box.createHorizontalBox();
    Box upDownBox = Box.createVerticalBox();
    Box showHideBox = Box.createVerticalBox();
    vBox = Box.createVerticalBox();
    upDownBox.add(upButton);
    upDownBox.add(downButton);
    showHideBox.add(showButton);
    showHideBox.add(hideButton);
    buttonBox.add(upDownBox);
    buttonBox.add(showHideBox);
    buttonBox.add(printButton);
    buttonBox.add(unionFrameButton);
    vBox.add(buttonBox);
    upButton.setText("Up");
    upButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSelectedStratum(++selectedStratum);
        setSelectedRadioButton(selectedStratum);
      }
    });
    downButton.setText("Down");
    downButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSelectedStratum(--selectedStratum);
        setSelectedRadioButton(selectedStratum);
      }
    });
    showButton.setText("Show");
    showButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showButton_actionPerformed(e);
      }
    });
    hideButton.setText("Hide");
    hideButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideButton_actionPerformed(e);
      }
    });
    printButton.setText("Print");
    printButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        printButton_actionPerformed(e);
      }
    });
    unionFrameButton.setText("Show Union Graph");
    unionFrameButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        unionFrameButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(hBox, null);
    vBox.add(radioButtons());
    hBox.add(vBox);
    hBox.add(drawingPanel);
    showAxisPlane();
  }
  JComponent radioButtons() {
    Box hBox = Box.createHorizontalBox();
    Box vBox = Box.createVerticalBox();
    String[] strataNames = (String[])root.getUserData();
    
    ActionListener a = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSelectedStratum(Integer.parseInt(e.getActionCommand()));
      }
    };
    // use a stack so we can add the buttons to the container in reverse order
    // ie, same as 3D stack
    JComponent[] buttonStack = new JComponent[strataNames.length];
    
    for(int i=0; i<strataNames.length; i++) {
      JRadioButton r = new JRadioButton(strataNames[i]);
      r.setActionCommand(""+i);
      r.addActionListener(a);
      radioButtonGroup.add(r);
      buttonStack[i]=r;
    }
    for(int i=strataNames.length-1;i>=0;i--) {
      vBox.add((JRadioButton)buttonStack[i]);
    }
    setSelectedRadioButton(0);
    hBox.add(vBox);
    return hBox;
  }
  ButtonGroup radioButtonGroup = new ButtonGroup();
  void setSelectedRadioButton(int selectedButton) {
    int i=0;
    Enumeration e = radioButtonGroup.getElements();
    JRadioButton selected = null;
    while(e.hasMoreElements() && i++<=selectedButton) {
      selected = (JRadioButton)e.nextElement();
    }
    radioButtonGroup.setSelected(selected.getModel(),true);
  }
  void setSelectedStratum(int selectedStratum) {
    this.selectedStratum = selectedStratum;
    downButton.setEnabled(selectedStratum==0?false:true);
    upButton.setEnabled(selectedStratum==(strataCount-1)?false:true);
    drawingPanel.setStratum(selectedStratum);
    Transform3D trans = new Transform3D();
    float extraSpace=0;
    for(int i=0;i<selectedStratum&&i<extraSpacing.size();i++) {
      extraSpace+=((Float)extraSpacing.get(i)).floatValue();
    }
    centroid.z = bottomLeft.z + strataSeparation * selectedStratum
           + extraSpace;
    trans.setTranslation(new Vector3f(centroid));
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
  
  void printButton_actionPerformed(ActionEvent e) {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(drawingPanel);
    PageFormat pf = printJob.pageDialog(printJob.defaultPage());
    if (printJob.printDialog()) {
      try {
        printJob.print();
      } catch (Exception ex) {
      }
    }
  }  
  void unionFrameButton_actionPerformed(ActionEvent e) {
    JFrame unionFrame = new JFrame("Union View"); 
    JPanel treePanel = new TreePanel();

    hBox = Box.createHorizontalBox();
    hBox.add(treePanel); 
    DrawingPanel unionDrawingPanel= new DrawingPanel(r, bottomLeft, topRight, DrawingPanel.RENDER_UNION);
    hBox.add(unionDrawingPanel);
    unionFrame.getContentPane().add(hBox);
    unionFrame.pack();
    unionFrame.setVisible(true); 
  }
  
  void showAxisPlane() {
    canvas.getTransformGroup().addChild(axisPlaneBG);
    showButton.setEnabled(false);
    hideButton.setEnabled(true);
    pack();
  }

  void kill() {
    show(false);
    axisPlaneBG.detach();
  }
  Point3f centroid, bottomLeft, topRight;
  Cluster r;
  float strataSeparation;
  int strataCount;
  int selectedStratum = 0;
  Vector extraSpacing;
  GraphCanvas canvas;
  GraphControl.ClusterFacade root;
  TransformGroup axisPlaneTG;
  BranchGroup axisPlaneBG;
  JButton upButton;
  JButton downButton;
  Box hBox, vBox;
  JButton showButton;
  JButton hideButton;
  JButton printButton;
  JButton unionFrameButton;
  DrawingPanel drawingPanel;
}
