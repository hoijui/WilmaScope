/*
 * Created on May 17, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.fadelayout;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.GraphElementView;
import org.wilmascope.view.ViewManager;
import org.wilmascope.viewplugin.LineNodeView;

/**
 * @author cmurray
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class FadeNodeLayout extends NodeLayout {
	private static final float maxMove = 0.3f;
	public static final float delta = 0.5f;
	private Vector3f force = new Vector3f(0.0f, 0.0f, 0.0f);
	public void setForce(float xf, float yf, float zf) {
		force.x = xf;
		force.y = yf;
		force.z = zf;
	}
	public void addForce(Vector3f f) {
		force.x += f.x;
		force.y += f.y;
		force.z += f.z;
	}
	public void scaleForce() {
		if (force.length() > maxMove) {
			force.normalize();
			force.scale(maxMove);
		}
		//force.scale(0.1f);
	}
	public Vector3f getForce() {
		return force;
	}
	public void applyForce() {
		Node node = getNode();
		if (node.isFixedPosition()) {
			return;
		}
		// Move the node
		node.reposition(force);
		
		if (VariableForces.layering == VariableForces.levelConstraint)
		{
			if (levelConstraint >= 0) {
				Point3f p = node.getPosition();
				p.z = layerScale * (float) levelConstraint;
			}
		}
		
		if (VariableForces.layering == VariableForces.degree)
		{			
			int degree = node.getDegree();
	    	
	    	if (VariableForces.inOnly)
	    	{
	    		degree = degree - node.getOutEdges().size();
	    	}
	    		
	    	if (degree >= degreeSplit1) {
				Point3f p = node.getPosition();
				p.z = layerScale * 0f;
			}
	    	else
	    	{
	    		if (degree >= degreeSplit2) {
					Point3f p = node.getPosition();
					p.z = layerScale * 3f;
				}
	    		else
	    		{
    				Point3f p = node.getPosition();
    				p.z = layerScale * 6f;
	    		}
	    	}
	    	
	    	if (!(node.getView() instanceof LineNodeView))
	    	{
	    		Point3f p = node.getPosition();
				p.z = layerScale * 9f;
	    	}
		}
		
		if (VariableForces.layering == VariableForces.spherelevelConstraint /*&& (node.getView() instanceof LineNodeView)*/)
		{
			float radius = 0f;
			
			if (levelConstraint >= 0) {
				radius +=  sphereScale*(float)(20-levelConstraint); 
			}
			
			Vector3f delta = new Vector3f(node.getPosition());
	    	float deltalength = delta.length();
	    	float diff = (deltalength - radius)/deltalength;
	    	delta.scale(diff);
	    	
	    	Vector3f new_pos = new Vector3f();
	    	new_pos.sub(node.getPosition(), delta);
	    	node.setPosition(new Point3f(new_pos));	
		}
		
		if (VariableForces.layering == VariableForces.sphereDegree)
		{			
			int degree = node.getEdges().size();
	    	
	    	if (VariableForces.inOnly)
	    	{
	    		degree = degree - node.getOutEdges().size();
	    	}
	    	
	    	float radius = 10f;
			
	    	if (degree >= degreeSplit1) {
				radius = 20f;
			}
	    	else
	    	{
	    		if (degree >= degreeSplit2) {
					radius = 15f;
				}
	    	}
	    	
	    	if (!(node.getView() instanceof LineNodeView))
	    	{
	    		radius = 25f;
	    	}
	    	
	    	Vector3f delta = new Vector3f(node.getPosition());
	    	float deltalength = delta.length();
	    	float diff = (deltalength - radius)/deltalength;
	    	delta.scale(diff);
	    	
	    	Vector3f new_pos = new Vector3f();
	    	new_pos.sub(node.getPosition(), delta);
	    	node.setPosition(new Point3f(new_pos));	
		}
		
		if (VariableForces.colouring == VariableForces.levelConstraintColouring && (node.getView() instanceof LineNodeView))
		{
			Color3f levelConstraintColour = new Color3f();
			if (levelConstraint == 0)
			{
				levelConstraintColour.set(0.0f, 0.8f, 0.8f);
			}
			if (levelConstraint == 1)
			{
				levelConstraintColour.set(1.0f, 1.0f, 0.2f);
			}
			if (levelConstraint == 2)
			{
				levelConstraintColour.set(1.0f, 0.8f, 0.8f);
			}
			if (levelConstraint == 3)
			{
				levelConstraintColour.set(0.8f, 0.0f, 0.8f);
			}
			if (levelConstraint == 4)
			{
				levelConstraintColour.set(0.0f, 0.2f, 0.2f);
			}
			if (levelConstraint == 5)
			{
				levelConstraintColour.set(0.0f, 0.0f, 0.8f);
			}
			if (levelConstraint == 6)
			{
				levelConstraintColour.set(1.0f, 0.4f, 1.0f);
			}
			if (levelConstraint == 7)
			{
				levelConstraintColour.set(0.8f, 0.0f, 0.2f);
			}
			if (levelConstraint == 8)
			{
				levelConstraintColour.set(0.8f, 0.6f, 0.0f);
			}
			if (levelConstraint == 9)
			{
				levelConstraintColour.set(1.0f, 0.4f, 0.0f);
			}
			if (levelConstraint == 10)
			{
				levelConstraintColour.set(0.2f, 1.0f, 0.0f);
			}
			if (levelConstraint == 11)
			{
				levelConstraintColour.set(0.7f, 0.1f, 0.6f);
			}
			setNodeColour(levelConstraintColour.x,levelConstraintColour.y, levelConstraintColour.z);
		}
		
		if (VariableForces.colouring == VariableForces.degreeColouring && (node.getView() instanceof LineNodeView))
		{
			
			int degree = node.getEdges().size();
	    	
	    	if (VariableForces.inOnly)
	    	{
	    		degree = degree - node.getOutEdges().size();
	    	}
			
	    	if (degree >= degreeSplit1) {
				setNodeColour(1.0f, 0.0f, 0.0f);
			}
	    	else
	    	{
	    		if (degree >= degreeSplit2) {
					setNodeColour(0.0f, 1.0f, 0.0f);
				}
	    		else
	    		{
    				setNodeColour(0.0f, 0.0f, 1.0f);
	    		}
	    	}
	    	
	    	/*if (!(node.getView() instanceof LineNodeView))
	    	{
	    		((GraphElementView)node.getView()).setColour(1.0f, 1.0f, 0.0f);
	    	}*/
		}
		
		String label = ((GraphElementView)node.getView()).getLabel();
		if (label != null && (label.equals(new String("George G. Robertson")) || (label.equals(new String("Stuart Card"))) || (label.equals(new String("Jean-Daniel Fekete"))) || (label.equals(new String("Georges Grinstein"))) || (label.equals(new String("Catherine Plaisant")))))
		{
			setNodeColour(1.0f, 1.0f, 0.0f);
		}
		
		/*Point3f p = node.getPosition();
		p.z = 0.0f;*/
		force.set(0.0f, 0.0f, 0.0f);
	}
	private void setNodeColour(float r, float g, float b) {
		((GraphElementView)getNode().getView()).setColour(r, g, b);
	}
	public void setLevelConstraint(int level) {
		levelConstraint = level;
	}
	/*public void setlevelConstraintColour(Color3f colour)
	{
		levelConstraintColour = colour;
	}*/
	
	int levelConstraint = -1;
	float layerScale = 1f;
	float sphereScale = 3f;
	int degreeSplit1 = 20;
	int degreeSplit2 = 10;
	
}