/*
 * Created on May 20, 2004
 *
 */
package org.wilmascope.fadelayout;

import javax.media.j3d.BoundingBox;
import org.wilmascope.graph.Node;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
//import javax.vecmath.Vector3f;
import java.util.Random;

/**
 * @author cmurray
 *
 */
public class OctTree
{
	private OctTreeCell root;
	
	private Random rand = new Random(System.currentTimeMillis());
	
	public OctTree(BoundingBox bBox)
	{
		root = new OctTreeCell(bBox);
	}
	
	public void addNode(Node node, OctTreeCell cell)
	{
		//System.out.println(node.getPosition().x + "  " + node.getPosition().y + " " + node.getPosition().z);
		/*Point3d u = new Point3d();
		cell.bBox.getUpper(u);
		Point3d l = new Point3d();
		cell.bBox.getLower(l);
		System.out.println(l.x + " " + l.y + " " + l.z + "  " + u.x + " " + u.y + " " + u.z);*/
		
		if (cell.isEmpty())
		{
			//System.out.println("empty");
			
			cell.contents = node;
			cell.centreOfMass.set(node.getPosition());
			cell.count = 1;
			cell.setStatusFull();
			
			return;
		}
		
		if (cell.isFull())
		{
			//System.out.println("full");
			cell.setStatusSplit();
			
			Node movingNode = cell.contents;
			cell.contents = null;
			cell.count = 0;
			cell.centreOfMass.set(0.0f, 0.0f, 0.0f);
			
			
			addNode(movingNode, cell);
			if (node.getPosition().equals(movingNode.getPosition()))
			{
				Point3d boxCentre = new Point3d();
				Point3d lower = new Point3d();
				cell.bBox.getLower(lower);
				Point3d upper = new Point3d();
				cell.bBox.getUpper(upper);
				boxCentre.add(lower, upper);
				boxCentre.scale(0.5f);
				Point3f bc = new Point3f(boxCentre);
				
				//if (bc.equals(node.getPosition()))
				//{
					Point3f moveOff = new Point3f(rand.nextFloat()*(root.width/2.0f)-(root.width/4.0f), rand.nextFloat()*(root.width/2.0f)-(root.width/4.0f), rand.nextFloat()*(root.width/2.0f)-(root.width/4.0f));
					node.setPosition(moveOff);
				//}
				//else
				//{
				//	node.setPosition(bc);
				//}
				addNode(node, root);
			}
			else
			{
				addNode(node, cell);
			}
		
			
			return;
		}
		
		if (cell.isSplit())
		{
			//System.out.println("split");
			cell.centreOfMass.x = ((((float)cell.count)*cell.centreOfMass.x) + node.getPosition().x)/(((float)cell.count)+1.0f);
			cell.centreOfMass.y = ((((float)cell.count)*cell.centreOfMass.y) + node.getPosition().y)/(((float)cell.count)+1.0f);
			cell.centreOfMass.z = ((((float)cell.count)*cell.centreOfMass.z) + node.getPosition().z)/(((float)cell.count)+1.0f);
			
			++cell.count;
			
			int index = indexOfDaughters(cell, node);
			//System.out.println(index);
			
			if (cell.daughters[index] == null)
			{
				BoundingBox bBox = boundBoxOfOctTant(cell, index);
				cell.daughters[index] = new OctTreeCell(bBox);
			}
			addNode(node, cell.daughters[index]);
		}
	}
	
	public OctTreeCell root()
	{
		return root;
	}
	
	private int indexOfDaughters(OctTreeCell cell, Node node)
	{
		Point3d boxCentre = new Point3d();
		Point3d lower = new Point3d();
		cell.bBox.getLower(lower);
		Point3d upper = new Point3d();
		cell.bBox.getUpper(upper);
		boxCentre.add(lower, upper);
		boxCentre.scale(0.5f);
		Point3f bc = new Point3f(boxCentre);
		Point3f nc = node.getPosition();
		
		//System.out.println(bc.x + " " + bc.y + " " + bc.z + " " + nc.x + " " + nc.y + " " + nc.z);
		
		if (nc.x <= bc.x && nc.y > bc.y && nc.z > bc.z)
			return 0;
		if (nc.x > bc.x && nc.y > bc.y && nc.z > bc.z)
			return 1;
		if (nc.x <= bc.x && nc.y <= bc.y && nc.z > bc.z)
			return 2;
		if (nc.x > bc.x && nc.y <= bc.y && nc.z > bc.z)
			return 3;
		if (nc.x <= bc.x && nc.y > bc.y && nc.z <= bc.z)
			return 4;
		if (nc.x > bc.x && nc.y > bc.y && nc.z <= bc.z)
			return 5;
		if (nc.x <= bc.x && nc.y <= bc.y && nc.z <= bc.z)
			return 6;
		//if (nc.x > bc.x && nc.y <= bc.y && nc.z <= bc.z)
		return 7;
	}
	
	private BoundingBox boundBoxOfOctTant(OctTreeCell cell, int octTant)
	{
		Point3d lower = new Point3d();
		cell.bBox.getLower(lower);
		Point3d upper = new Point3d();
		cell.bBox.getUpper(upper);
		
		double newCellSize = (double)cell.width/2.0;
		Point3d lMove = new Point3d();
		Point3d uMove = new Point3d();
		
		if (octTant == 0)
		{
			lMove.set(0.0, 1.0, 1.0);
			uMove.set(-1.0, 0.0, 0.0);
		}
		if (octTant == 1)
		{
			lMove.set(1.0, 1.0, 1.0);
			uMove.set(0.0, 0.0, 0.0);
		}
		if (octTant == 2)
		{
			lMove.set(0.0, 0.0, 1.0);
			uMove.set(-1.0, -1.0, 0.0);	
		}
		if (octTant == 3)
		{
			lMove.set(1.0, 0.0, 1.0);
			uMove.set(0.0, -1.0, 0.0);
		}
		if (octTant == 4)
		{
			lMove.set(0.0, 1.0, 0.0);
			uMove.set(-1.0, 0.0, -1.0);		
		}
		if (octTant == 5)
		{
			lMove.set(1.0, 1.0, 0.0);
			uMove.set(0.0, 0.0, -1.0);
		}
		if (octTant == 6)
		{
			lMove.set(0.0, 0.0, 0.0);
			uMove.set(-1.0, -1.0, -1.0);
		}
		if (octTant == 7)
		{
			lMove.set(1.0, 0.0, 0.0);
			uMove.set(0.0, -1.0, -1.0);
		}
		
		lMove.scale(newCellSize);
		uMove.scale(newCellSize);
		
		lMove.add(lower);
		uMove.add(upper);
		
		return new BoundingBox(lMove, uMove);
	}
}
