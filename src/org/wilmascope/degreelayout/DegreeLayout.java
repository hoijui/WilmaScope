/*
 * Created on May 17, 2004
 *
 */
package org.wilmascope.degreelayout;

import java.util.Properties;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.swing.JPanel;
import javax.media.j3d.BoundingBox;


import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.EdgeLayout;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeLayout;
import org.wilmascope.graph.NodeList;
import org.wilmascope.control.GraphControl;

import org.wilmascope.fadelayout.OctTree;
import org.wilmascope.fadelayout.OctTreeCell;
import org.wilmascope.view.GraphCanvas;
import org.wilmascope.view.GraphElementView;

/**
 * @author cmurray
 *
 */
public class DegreeLayout implements LayoutEngine {
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#calculateLayout()
	 */
	
	Cluster root;
	private final float balancedThreshold = 0.00001f;
	private int iterations = 0;
	//private final float forceIntensity = 0.1f;
	//private final float openingCriterion = 0.7f;
	
	NodeList L;
	NodeList L2;
	NodeList L3;
	
	EdgeList EL;
	EdgeList EL2;
	EdgeList EL3;
	
	GraphCanvas gc;
		
	
	public void calculateLayout()
	{
		
		if (iterations == 0)
		{
			partition();
		}
		if (iterations == 500)
		{
			for (int i = 0; i != L.size(); ++i)
		    {
		    	Node n = (Node)L.get(i);
		    	n.setFixedPosition(true);
		    }
			for (int i = 0; i != L2.size(); ++i)
			{
				Node n = L2.get(i);
				n.getView().show(gc);
				((DegreeNodeLayout)(n.getLayout())).setLevelConstraint(5);
			}
			for (int i = 0; i != EL2.size(); ++i)
			{
				Edge e = EL2.get(i);
				e.getView().show(gc);
			}
			L.addAll(L2);
			EL.addAll(EL2);
		}
		
		if (iterations == 1000)
		{
			for (int i = 0; i != L.size(); ++i)
		    {
		    	Node n = (Node)L.get(i);
		    	n.setFixedPosition(true);
		    }
			for (int i = 0; i != L3.size(); ++i)
			{
				Node n = L3.get(i);
				n.getView().show(gc);
    			((DegreeNodeLayout)(n.getLayout())).setLevelConstraint(11);
			}
			for (int i = 0; i != EL3.size(); ++i)
			{
				Edge e = EL3.get(i);
				e.getView().show(gc);
			}
			L.addAll(L3);
			EL.addAll(EL3);
		}
		
		/*FadeNodeLayout nodeLayout;
		
		java.util.Random r = new java.util.Random(System.currentTimeMillis());
		
		NodeList nodes = root.getNodes();
	    for (nodes.resetIterator(); nodes.hasNext();) {
	      nodeLayout = (FadeNodeLayout) (nodes.nextNode().getLayout());
	      nodeLayout.setForce(r.nextFloat()/100.0f, r.nextFloat()/100.0f, r.nextFloat()/100.0f);
	    }*/
		
		// Compute OctTree
	    NodeList nodes = root.getNodes();
	    
	    // Calculate Bounding Box for nodes
	    double max = 1.0;
	    for (Node n:nodes)
	    {
	    	double coord = (double)Math.abs(n.getPosition().x);
	    	if (coord > max)
	    		max = coord;
	    	coord = Math.abs(n.getPosition().y);
	    	if (coord > max)
	    		max = coord;
	    	coord = Math.abs(n.getPosition().z);
	    	if (coord > max)
	    		max = coord;
	    }
	    
	    Point3d lower = new Point3d(-max, -max, -max);
	    Point3d upper = new Point3d(max, max, max);
	    BoundingBox bBox = new BoundingBox(lower, upper);
	    
	    OctTree tree = new OctTree(bBox);
	    //System.out.println(iterations);
	    for (Node n:nodes)
	    {
	    	tree.addNode(n, tree.root());
	    	//System.out.println();
	    }
		
		if (VariableForces.edgeForce)
		{
			// Compute edge forces
		    EdgeList edges = EL;
		    float damping = 2.0f; // damping >= 2
		    for (Edge currentEdge:edges)
		    {
		    	DegreeEdgeLayout edgeLayout = (DegreeEdgeLayout)currentEdge.getLayout();
		    	float resilience = VariableForces.edgeResilience;
		    	float idealLength = edgeLayout.idealLength;
		    	
		    	Node start = currentEdge.getStart();
		    	Node end = currentEdge.getEnd();
		    	
		    	Vector3f edgeForce = new Vector3f();
		    	edgeForce.sub(end.getPosition(), start.getPosition());
		    	float distance = edgeForce.length();
		    	
		    	if (distance != 0.0f)
		    	{
		    		float springFactor = ((idealLength - distance) * resilience)/(damping*distance);
		    		
		    		edgeForce.scale(springFactor);
		    		    		    	
			    	((DegreeNodeLayout)(end.getLayout())).addForce(edgeForce);
			    	edgeForce.scale(-1.0f);
			    	((DegreeNodeLayout)(start.getLayout())).addForce(edgeForce);
			    	
		    	}
		    }
		}
		
		/*//		 repelling edges version
		if (VariableForces.edgeForce)
		{
			// Compute edge forces
		    EdgeList edges = root.getInternalEdges();
		    float damping = 2.0f; // damping >= 2
		    for (edges.resetIterator(); edges.hasNext();)
		    {
		    	Edge currentEdge = edges.nextEdge();
		    	DegreeEdgeLayout edgeLayout = (DegreeEdgeLayout)currentEdge.getLayout();
		    	float resilience = VariableForces.edgeResilience;
		    	float idealLength = edgeLayout.idealLength;
		    	
		    	Node start = currentEdge.getStart();
		    	Node end = currentEdge.getEnd();
		    	
		    	Vector3f repelForce = new Vector3f();
		    	repelForce.sub(start.getPosition(), end.getPosition());
		    	float distance = repelForce.length();
		    	if (distance != 0)
		    	{
		    		float repelFactor = VariableForces.edgeResilience/(distance*distance);
	    			
	    			repelForce.scale(repelFactor);
			    	
	    			//((DegreeNodeLayout)(start.getLayout())).addForce(repelForce);
	    			repelForce.scale(-1.0f);
			    	((DegreeNodeLayout)(end.getLayout())).addForce(repelForce);
		    	}
		    }
		}*/
	    
	    
	    if (VariableForces.nonEdgeForce)
	    {
		    // 	Compute non-edge forces (node-node and node-pseudonode) using octtree   
		    for (int i = 0; i != nodes.size(); ++i)
		    {
		        approxForce(nodes.get(i), tree.root());
		    }
	    }
	    
	    if (VariableForces.originForce)
	    {
		    float damping = 2.0f; // damping >= 2
		    float intensity = VariableForces.originIntensity;
	    	float idealLength = 0.0f;
		    for (int i = 0; i != nodes.size(); ++i)
		    {
		        Node n = nodes.get(i);
		        
		        Vector3f originForce = new Vector3f(n.getPosition());
		    	float distance = originForce.length();
		    	
		    	if (distance != 0.0f)
		    	{
		    		float attractFactor = ((-distance)*intensity)/(damping*distance);
		    		
		    		originForce.scale(attractFactor);
		    		    		    	
			    	((DegreeNodeLayout)(n.getLayout())).addForce(originForce);
			    	
		    	}
		    }
	    }
	    
	    /*Node node1;
	    Node node2;
	    
	    NodeList nodes = root.getNodes();
	    
	    for (int i = 0; i != nodes.size(); ++i)
	    {
	        node1 = nodes.get(i);
	        
	        for (int j = 0; j != nodes.size(); ++j)
	        {
	        	if (j != i)
	        	{
	        		node2 = nodes.get(j);
	        		EdgeList node1edges = node1.getEdges();
	        		int k = 0;
	        		for (k = 0; k != node1edges.size(); ++k)
	        		{
	        			if (node1edges.get(k).getNeighbour(node1) == node2)
	        				break;
	        		}
	        		
	        		if (k == node1edges.size())
	        		{	        		
		        		Vector3f repelForce = new Vector3f();
		        		repelForce.sub(node1.getPosition(), node2.getPosition());
		        		float distance = repelForce.length();
		        		
		        		if (distance != 0)
		        		{
		        			float repelFactor = (FadeNodeLayout.delta*FadeNodeLayout.delta)/(distance*distance);
		        			repelForce.scale(repelFactor);
		        			
		        			((FadeNodeLayout)(node1.getLayout())).addForce(repelForce);
		        		}
	        		}
	        	}
	        }
	    }*/  
	}
	
	private void partition()
	{
		L = new NodeList();
		L2 = new NodeList();
		L3 = new NodeList();
		
		int degreeSplit1 = 20;
		int degreeSplit2 = 10;
		
		NodeList nodes = root.getNodes();
		if (nodes.size() != 0)
		{
			gc = ((GraphElementView)nodes.get(0).getView()).getGraphCanvas();
		}
		
		
		for (Node n:nodes)
	    {
	    	n.setFixedPosition(false);
	    	
	    	int degree = n.getEdges().size();
	    	
	    	if (VariableForces.inOnly)
	    	{
	    		degree = degree - n.getOutEdges().size();
	    	}
	    	
	    	if (degree >= degreeSplit1)
	    	{
	    		((GraphElementView)n.getView()).setColour(1.0f, 0.0f, 0.0f);
	    		((DegreeNodeLayout)(n.getLayout())).setLevelConstraint(0);
	    		L.add(n);
	    	}
	    	else
	    	{
	    		n.getView().hide();
	    		if (degree >= degreeSplit2)
	    		{
	    			((GraphElementView)n.getView()).setColour(0.0f, 1.0f, 0.0f);
	    			//((DegreeNodeLayout)(n.getLayout())).setLevelConstraint(5);
	    			L2.add(n);
	    		}
	    		else
	    		{
	    			((GraphElementView)n.getView()).setColour(0.0f, 0.0f, 1.0f);
	    			//((DegreeNodeLayout)(n.getLayout())).setLevelConstraint(11);
	    			L3.add(n);
	    		}
	    	}
	    }
		
		
		EL = new EdgeList();
		EL2 = new EdgeList();
		EL3 = new EdgeList();
		
		EdgeList edges = root.getInternalEdges();
		for (Edge currentEdge:edges)
		{
		    Node start = currentEdge.getStart();
	    	Node end = currentEdge.getEnd();
	    	
	    	int startDegree = start.getEdges().size();
	    	int endDegree = end.getEdges().size();
	    	
	    	if (VariableForces.inOnly)
	    	{
	    		startDegree = startDegree - start.getOutEdges().size();
	    		endDegree = endDegree - end.getOutEdges().size();
	    	}
	    	
	    	int degree = Math.min(startDegree, endDegree);
	    	
	    	if (degree >= degreeSplit1)
	    	{
	    		EL.add(currentEdge);
	    	}
	    	else
	    	{
	    		currentEdge.getView().hide();
	    		if (degree >= degreeSplit2)
	    		{
	    			EL2.add(currentEdge);
	    		}
	    		else
	    		{
	    			EL3.add(currentEdge);
	    		}
	    	}
		}
	}
	
	private void approxForce(Node node, OctTreeCell cell)
    {
    	
    	Vector3f repelForce = new Vector3f();
    	repelForce.sub(node.getPosition(), cell.centreOfMass);
    	float distance = repelForce.length();
    	float M = 0.0f;
    	if (distance != 0)
    	{
    		if (VariableForces.openingCriterion.equals(new String("bh")))
    		{
    			// criterion type is barnes hut
    			M = cell.width/distance;
    			//System.out.println("bh");
    		}
    		
    		if (VariableForces.openingCriterion.equals(new String("newbh")))
    		{
    			// criterion type is New Barnes-Hut
    			Point3d boxCentre = new Point3d();
				Point3d lower = new Point3d();
				cell.bBox.getLower(lower);
				Point3d upper = new Point3d();
				cell.bBox.getUpper(upper);
				boxCentre.add(lower, upper);
				boxCentre.scale(0.5f);
				Point3f bc = new Point3f(boxCentre);
    			
    			Vector3f C = new Vector3f();
    			C.sub(cell.centreOfMass, bc);
    			
    			M = cell.width/(distance - C.lengthSquared());
    			//System.out.println("newBh");
    		}
    		
    		if (VariableForces.openingCriterion.equals(new String("od")))
    		{
    			// criterion type is orthogonal distance
    			float V = Math.max(Math.abs(repelForce.x), Math.abs(repelForce.y));
    			V = Math.max(V, Math.abs(repelForce.z));
    			
    			M = cell.width/(V - 0.5f*cell.width);
    			//System.out.println("od");
    			
    		}
    		
    		if (M <= VariableForces.accuracyParameter || cell.isLeaf())
    		{
    			float repelFactor = ((float)cell.count)*VariableForces.repelIntensity/(distance*distance);
    			
    			repelForce.scale(repelFactor);
		    	
    			((DegreeNodeLayout)(node.getLayout())).addForce(repelForce);
    		}
    		else
    		{
    			for (int i = 0; i != 8; ++i)
    			{
    				if (cell.daughters[i] != null)
    				{
    					approxForce(node, cell.daughters[i]);
    				}
    			}
    		}
    	}
    	
    }
	
	
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#applyLayout()
	 */
	public boolean applyLayout() {
		
		++iterations;
		System.out.println(iterations);
		
		// Move nodes according to calculated forces
		float maxForce = 0.0f;
		
		DegreeNodeLayout nodeLayout;
	    
	    NodeList nodes = root.getNodes();
	    EdgeList edges = root.getInternalEdges();
	    for (Node n:nodes)
	    {
	      nodeLayout = (DegreeNodeLayout) (n.getLayout());
	      float forceSize = nodeLayout.getForce().length();
	      if (forceSize > maxForce)
	      {
	      	maxForce = forceSize;
	      }
	      nodeLayout.scaleForce();
	      nodeLayout.applyForce();    
	    }
	    
	    for (int i = 0; i != edges.size(); i++)
	    {
	      edges.get(i).recalculate();
	    }
	    
	    if (/*maxForce < balancedThreshold ||*/ iterations > 1500)
	    {
	    	iterations = 0;
	    	return true;
	    }
	    
	    return false;
	   
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#getName()
	 */
	public String getName() {
		return "Degree Layout";
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#createNodeLayout(org.wilmascope.graph.Node)
	 */
	public NodeLayout createNodeLayout(Node n) {
		return new DegreeNodeLayout();
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#createEdgeLayout(org.wilmascope.graph.Edge)
	 */
	public EdgeLayout createEdgeLayout(Edge e) {
		return new DegreeEdgeLayout();
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#getProperties()
	 */
	public Properties getProperties() {
		return new Properties();
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties p) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#getControls()
	 */
	public JPanel getControls() {
		return new DegreeControlsPanel((GraphControl.Cluster) root.getUserFacade());
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#init(org.wilmascope.graph.Cluster)
	 */
	public void init(Cluster root) {
		this.root = root;
		createElementLayouts();
	}
	/* (non-Javadoc)
	 * @see org.wilmascope.graph.LayoutEngine#create()
	 */
	public LayoutEngine create() {
		return new DegreeLayout();
	}
	
	public void createElementLayouts() {
	    NodeList nodes = root.getNodes();
	    //Random rand = new Random(System.currentTimeMillis());
	    for (Node n : nodes) {
	      n.setLayout(createNodeLayout(n));
	      //n.setPosition(new Point3f(rand.nextFloat()*20.0f-10.0f, rand.nextFloat()*20.0f-10.0f, rand.nextFloat()*20.0f-10.0f));
	    }
	    EdgeList edges = root.getInternalEdges();
	    for (Edge e:edges) {
	      e.setLayout(createEdgeLayout(e));
	      //e.recalculate();
	    }
	}
}
