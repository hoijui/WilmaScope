package org.wilmascope.viewplugin;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.swing.ImageIcon;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.wilmascope.view.Colours;
import org.wilmascope.view.CompoundEdgeView;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.NodeView;

/**
 * An edge view that has bends at the specified positions.
 * The bends must be assigned by the layout engine.
 * The edges are coloured lines for fast rendering
 *
 * @author Tristan Manwaring, adapted from Tim Dwyer
 * @version 1.0
 */
public class BendyLineEdgeView extends EdgeView implements CompoundEdgeView {
  
  public BendyLineEdgeView() {
    setTypeName("Bendy Line Edge");
  }
  protected void setupDefaultMaterial() {
    setupDefaultAppearance(Colours.blueMaterial);
  }
  
  protected void setupHighlightMaterial() {
    setupHighlightAppearance(Colours.yellowMaterial);
  }
  
  boolean isChanged; //true if the edge has been changed, requiring a redraw
  LineArray myLine; //the line array
  NodeView startView; //the view of the start node
  NodeView endView; //the view of the end node
  
  int settings = GeometryArray.ALLOW_COORDINATE_WRITE
  | GeometryArray.ALLOW_COLOR_WRITE
  | GeometryArray.COORDINATES
  | GeometryArray.COLOR_3;
  
  /**
   * Must be called on creation and each time the BendyLineEdgeView completely changes.
   * This allows a new set of bends to be entered.
   */
  public void init() {
    bends = new ArrayList<Point3f>();
    isChanged = true;
    startView = (NodeView)getEdge().getStart().getView();
    endView = (NodeView)getEdge().getEnd().getView();
  }
  
  /**
   * Must be called when either the start node or the end node's colour is changed
   * this allows a redraw without re-entering the bend points again
   */
  public void colourChange(){
	  isChanged = true;
	  startView = (NodeView)getEdge().getStart().getView();
	  endView = (NodeView)getEdge().getEnd().getView();
  }
  
  public void setChanged(boolean changed) {
	  isChanged = changed;
  }
  
  public ArrayList<Point3f> getBends() {
	  return bends;
  }
  
  public void setBends(ArrayList<Point3f> _bends) {
	  bends = _bends;
	  isChanged = true;
  }
  
  /**
   * A bend point, should be specified by the layout engine
   * @see org.wilmascope.view.CompoundEdgeView#addBend(javax.vecmath.Point3f)
   */
  public void addBend(Point3f bendPosition) {
    bends.add(bendPosition);
  }
  
  /**
   * Draws the edge if the edge is changed (isChanged is true)
   */
  public void draw() {
	  if(isChanged) {
		  if (bg != null) {
	        bg.detach();
	      }
	      bg = new BranchGroup();
	      bg.setCapability(BranchGroup.ALLOW_DETACH);
	      Point3f start = getEdge().getStart().getPosition();
	      
	      //initialise the line array
	      myLine = new LineArray((bends.size() + 2) * 2, settings);
	      myLine.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
	      myLine.setCapability(LineArray.ALLOW_COLOR_WRITE);
	      
	      //add the lines
	      int i;
	      for(i = 0; i < bends.size(); ++i){
	        addLine(start, bends.get(i), i);
	        start = bends.get(i);
	      }
	      addLine(start, getEdge().getEnd().getPosition(), i);
	      
	      //convert the lineArray into a pickable shape
	      Shape3D s = new Shape3D(myLine);
	      makePickable(s);
	      bg.addChild(s);
	      
	      addLiveBranch(bg);   
	  }
      
	  //do not redraw unless it is changed (init() is called)
      isChanged = false;
  }
  
  public void setStartColour(Color3f c, int index) {
	    myLine.setColor(index, c);
  }
  public void setEndColour(Color3f c, int index) {
	  	myLine.setColor(index, c);
  }
  
  private void addLine(Point3f point_a, Point3f point_b, int index) {
	  index = index * 2;
	  //set the colours for each individual line in the line array
	  setStartColour(startView.getColor3f(), index);
      setEndColour(endView.getColor3f(), index + 1);
      myLine.setCoordinate(index, point_a);
      myLine.setCoordinate(index + 1, point_b);
  }
  
  public ImageIcon getIcon() {
    return new ImageIcon(org.wilmascope.images.Images.class.getResource("bendyedge.png"));
  }  
  public void setProperties(Properties p) {
    super.setProperties(p);
    String bendsProperty = p.getProperty("Bends");
    if(bendsProperty!=null) {
      StringTokenizer bendsStr = new StringTokenizer(bendsProperty, ",");
      do {
        float x = Float.parseFloat(bendsStr.nextToken());
        float y = Float.parseFloat(bendsStr.nextToken());
        float z = Float.parseFloat(bendsStr.nextToken());
        Point3f point = new Point3f(x,y,z);
        bends.add(point);
      }while(bendsStr.hasMoreTokens());
    }
  }
  
  public Properties getProperties() {
    Properties p = super.getProperties();
    if (bends.size() > 0) {
      String bendStr = bends.get(0).x+","+bends.get(0).y+","+bends.get(0).z;
      for(int i = 1; i < bends.size(); i++) {
        bendStr=bendStr+","+bends.get(i).x+","+bends.get(i).y+","+bends.get(i).z;
      }
      p.setProperty("Bends",bendStr);
    }
    return p;
  }
  ArrayList<Point3f> bends;
  BranchGroup bg = null;
}

