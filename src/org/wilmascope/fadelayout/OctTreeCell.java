/*
 * Created on May 20, 2004
 *
 */
package org.wilmascope.fadelayout;

import javax.media.j3d.BoundingBox;
import org.wilmascope.graph.Node;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;

/**
 * @author cmurray
 *
 */

public class OctTreeCell
{
	private static final int empty = 0;
	private static final int full = 1;
	private static final int split = 2;
	
	
	public BoundingBox bBox;
	public Node contents;
	
	private int status;
	public int count;
	
	public OctTreeCell[] daughters;
	
	public Point3f centreOfMass;
	
	public float width;
	// OctTreeCell children[];
	// int weight;
	
	public OctTreeCell(BoundingBox bBox)
	{
		this.bBox = bBox;
		
		status = empty;
		count = 0;
		
		daughters = new OctTreeCell[8];
		
		centreOfMass = new Point3f();
		
		Point3d lower = new Point3d();
		bBox.getLower(lower);
		Point3d upper = new Point3d();
		bBox.getUpper(upper);
		
		width = (float)(Math.abs(upper.x - lower.x));
	}
	
	public boolean isEmpty()
	{
		return status == empty;
	}
	
	public void setStatusEmpty()
	{
		status = empty;
	}
	
	public boolean isFull()
	{
		return status == full;
	}
	
	public void setStatusFull()
	{
		status = full;
	}
	
	public boolean isSplit()
	{
		return status == split;
	}
	
	public void setStatusSplit()
	{
		status = split;
	}
	
	public boolean isLeaf()
	{
		return isFull();
	}
}
