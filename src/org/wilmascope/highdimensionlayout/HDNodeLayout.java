/*
 * Created on Jun 10, 2004
 *
 */
package org.wilmascope.highdimensionlayout;

import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;

import javax.vecmath.Point3f;

/**
 * @author cmurray
 *
 */
public class HDNodeLayout extends NodeLayout
{
	public int graphTheoreticDist;
	public Point3f HDPos;
	
	public HDNodeLayout()
	{
		graphTheoreticDist = -1;
		HDPos = new Point3f();
	}
	
	public void setPos()
	{
		Node node = getNode();
		
		node.setPosition(HDPos);
	}
}
