/*
 * Created on 24/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.wilmascope.graphmodifiers.plugin;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.vecmath.Point3f;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import org.wilmascope.control.*;
import org.wilmascope.control.GraphControl.Node;
import org.wilmascope.control.GraphControl.Edge;
import org.wilmascope.control.GraphControl.Cluster;
import org.wilmascope.graphmodifiers.GraphModifier;
import org.wilmascope.gui.SpinnerSlider;
import org.wilmascope.view.EdgeView;
import org.wilmascope.viewplugin.LineEdgeView;

/**
 * @author aahmed
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NavAssist_Plus extends GraphModifier implements Runnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.util.Plugin#getName()
	 */
	
	class PhantomNodeController extends Thread
	{
		protected NavAssist_Plus boss;
		protected Point3f startPosition;
		protected boolean done = false;
		
		public PhantomNodeController(NavAssist_Plus boss, Point3f startPosition)
		{
			this.boss = boss;
			this.startPosition = startPosition;
		}
		
		public void ternimate()
		{
			this.done = true;
		}
		
		public void run()
		{
			float interval = 0.05f;
			Point3f newPosition;
			boss.getPhantomNode().setPosition(startPosition);
			while (!done)
			{
				newPosition = boss.getPhantomNode().getPosition();
				newPosition.x += interval;
				boss.getPhantomNode().setPosition(newPosition);
				try
				{
					Thread.sleep(20);
				}
				catch (Exception oops)
				{
					System.out.println("NodeControlThread: " + oops.toString());
				}
			}
		}
	}

	JPanel controlPanel = new JPanel();
	final SpinnerSlider numberSlider;
	final SpinnerSlider weightSlider;
	final SpinnerSlider radiusSlider;
	final SpinnerSlider phantomNodeXPositionSlider;
	final JButton stopButton;

	float effectiveRadius = 20.0f;
	float edgeWeight = 1.0f;
	float edgeRadius = 0.001f;
	int counterValue = 50;
	
	// set properties for the phantom edges		
	Properties phantomEdgeProperty = new Properties();

	private float limitMin = 10000, limitMax = -99999;

	PhantomNodeController phantomNodeController = null;
	Thread navThread = null;

	Cluster root = null;
	protected GraphControl.Node phantomNode = null;
	
	private ArrayList<Edge> phantomEdges;

	public NavAssist_Plus() {
		numberSlider = new SpinnerSlider(
				"Effective Radius", 1, 100, (int) effectiveRadius);
		weightSlider = new SpinnerSlider("Edge Weight", 0, 100, (int) (edgeWeight * 100));
		radiusSlider = new SpinnerSlider("Edge Radius", 0, 100, (int) (edgeRadius * 1000));
		phantomNodeXPositionSlider = new SpinnerSlider("Node X Position", 0, 100, counterValue);
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		
		phantomNodeXPositionSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				Point3f pos;
				float reductionFactor = 10.0f;
				if (phantomNodeXPositionSlider.getValue() > counterValue)
				{
					// move to the positive side
					pos = getPhantomNode().getPosition();
					pos.x += Math.abs(phantomNodeXPositionSlider.getValue() - counterValue) / reductionFactor;
					getPhantomNode().setPosition(pos);
				}
				else
				{
					// move to the negitave side
					pos = getPhantomNode().getPosition();
					pos.x -= Math.abs(counterValue - phantomNodeXPositionSlider.getValue()) / reductionFactor;
					getPhantomNode().setPosition(pos);
				}
				counterValue = phantomNodeXPositionSlider.getValue();
			}
		});
		
		numberSlider.addChangeListener(new ChangeListener() {
			synchronized public void  stateChanged(ChangeEvent e) {
				effectiveRadius = numberSlider.getValue() / 10.0f;
			}
		});
		
		weightSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				if (phantomEdges == null) return;
				root.freeze();
				edgeWeight = weightSlider.getValue() / 100.0f;
				msg("edgeWeight: " + edgeWeight);
				resetProperties();
				for (Edge anEdge : phantomEdges)
					// anEdge.getView().setProperties(phantomEdgeProperty);
					anEdge.setWeight(edgeWeight);
				root.unfreeze();
			}
		});
		
		radiusSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (phantomEdges == null) return;
				root.freeze();
				edgeRadius = radiusSlider.getValue() / 1000.0f;
				msg("edgeRadius: " + edgeRadius);
				resetProperties();
				for (Edge anEdge : phantomEdges)
					anEdge.getView().setProperties(phantomEdgeProperty);
				root.unfreeze();
			}
		});
		
		stopButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// cleanup phantomNode
						phantomNodeController.ternimate();
						phantomNodeController = null;
						getPhantomNode().delete();
						// cleanup phantomEdges
						Edge temp;
						for (int i = 0; i < phantomEdges.size(); i++)
						{
							temp = phantomEdges.get(i);
							phantomEdges.remove(i);
							temp.delete();
						}
						// clean up myself
						navThread = null;
						stopButton.setEnabled(false);
					}
				});
		controlPanel.setLayout(new GridLayout(2,2));
		controlPanel.add(numberSlider);
		controlPanel.add(stopButton);
		controlPanel.add(weightSlider);
		controlPanel.add(radiusSlider);
		controlPanel.add(phantomNodeXPositionSlider);
		
		// set up properties
		this.resetProperties();
	}
	
	private void resetProperties()
	{
		// set up phantomEdges Properties
		phantomEdgeProperty.setProperty("Weight", Float.toString(edgeWeight));
		phantomEdgeProperty.setProperty("Radius", Float.toString(edgeRadius));
		phantomEdgeProperty.setProperty("PhantomEdge", "true");
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "NavAssist+";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.graphmodifiers.GraphModifier#modify(org.wilmascope.control.GraphControl.Cluster)
	 */
	public void modify(Cluster cluster) {
		root = cluster;
		if (navThread == null) {
			navThread = new Thread(this, "NavAssist+");
			navThread.start();
			stopButton.setEnabled(true);
		}
	}
	
	synchronized Node getPhantomNode()
	{
		return this.phantomNode;
	}
	
	synchronized void setPhantomNode(Node aNode)
	{
		this.phantomNode = aNode;
	}

	public void run() {
		Thread myThread = Thread.currentThread();
		Node[] neighbors, newNeighbors, oldNeighbors;
		Node[] allNodes = root.getNodes();			
		Random rnd = new Random(Calendar.getInstance().getTimeInMillis());
		Iterator<Edge> it;
		phantomEdges = new ArrayList<Edge>();

		// calculate MinMax
		calculateMinMax(allNodes);
		
		// add phantom node
		this.setPhantomNode(root.addNode());
		this.getPhantomNode().setRadius(0.05f);
		Point3f phantomNodePosition = new Point3f(limitMin, 0.0f, 0.0f);
		// origin
		phantomNodePosition.x = 0.0f;
		this.getPhantomNode().setPosition(phantomNodePosition);
		
		// fix node
		this.getPhantomNode().setProperty("FixedPosition", "True");
		
		// start node controller thread
		// phantomNodeController = new PhantomNodeController(this, phantomNodePosition);
		// phantomNodeController.start();

		// calculate neighbor set
		neighbors = getNeighbors(allNodes);

		// add edges
		Edge anEdge;
		for (Node aNode : neighbors)
		{
			anEdge = root.addEdge(aNode, this.getPhantomNode());
			anEdge.getView().setProperties(phantomEdgeProperty);
			anEdge.setWeight(1.0f);
			phantomEdges.add(anEdge);
		}
	
		while (navThread == myThread)
		{
			// calculate new naighbors to be added (became close)
			newNeighbors = getNewNeighbors(allNodes, neighbors);
			
			// calculate old neighbors to be deleted (went too far)
			oldNeighbors = getOldNeighbors(neighbors);
			
			// remove edges to oldNeighbors
			ArrayList<Edge> removeList = new ArrayList<Edge>();
			if (oldNeighbors.length > 0)
			{
				it = phantomEdges.iterator();
				Edge temp;
				Node startNode, endNode;
				while (it.hasNext())
				{
					temp = it.next();
					startNode = temp.getStartNode();
					endNode = temp.getEndNode();
					for (Node aNode : oldNeighbors)
						if (startNode == aNode || endNode == aNode)
							removeList.add(temp);
				}
				for (int i = 0; i < removeList.size(); i++)
				{
					phantomEdges.remove(removeList.get(i));
					removeList.get(i).delete();
				}
			}

			// add edges to newNeighbors
			for (Node aNode : newNeighbors)
			{
				anEdge = root.addEdge(aNode, this.getPhantomNode());
				anEdge.getView().setProperties(phantomEdgeProperty);
				anEdge.setWeight(1.0f);
				phantomEdges.add(anEdge);
			}
			// msg("new " + newNeighbors.length + " edges added");
			
			// update neighbors
			// msg("old neighbor count is " + neighbors.length);
			ArrayList<Node> nList = this.convertArrayToList(neighbors);
			ArrayList<Node> newList = this.convertArrayToList(newNeighbors);
			ArrayList<Node> oldList = this.convertArrayToList(oldNeighbors);
			
			nList.removeAll(oldList);
			nList.addAll(newList);
			
			neighbors = this.convertToNodeArray(nList);
			// msg("New neighbor count is " + neighbors.length);
			
			// unfreez
			root.unfreeze();
			
			// update message area
			// for debug purpose
			// msg("Effective radius is " + effectiveRadius);
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException oops){
				}
			}
	}
	
	protected Node[] getNewNeighbors(Node[] allNodes, Node[] neighbors)
	{
		// calculate nonNeighbors
		ArrayList<Node> nonNeighbors = new ArrayList<Node>();
		for (Node aNode : allNodes)
			if (!contains(neighbors, aNode)) nonNeighbors.add(aNode);
			
		// identify new neighbors
		ArrayList<Node> newNeighborsList = new ArrayList<Node>();
		Iterator<Node> it = nonNeighbors.iterator();
		Node temp;
		while (it.hasNext())
		{
			temp = it.next();
			if (isNeighbor(temp, this.getPhantomNode()))
				newNeighborsList.add(temp);
		}
		// return node array
		return this.convertToNodeArray(newNeighborsList);
	}
	
	protected Node[] getOldNeighbors(Node[] neighbors)
	{
		// identify outcasts
		ArrayList<Node> outCasts = new ArrayList<Node>();
		for (Node aNode : neighbors)
			if (!isNeighbor(aNode, this.getPhantomNode()))
				outCasts.add(aNode);
		
		// return node array
		
		return this.convertToNodeArray(outCasts);
	}
	
	protected ArrayList<Node> convertArrayToList(Node[] nodeArray)
	{
		ArrayList<Node> retList = new ArrayList<Node>();
		for (Node aNode : nodeArray)
			retList.add(aNode);
		
		return retList;
	}
	
	protected Node[] convertToNodeArray(ArrayList<Node> nodeList)
	{
		Iterator<Node> it;
		Node[] retArray = new Node[nodeList.size()];
		it = nodeList.iterator();
		for (int i = 0; it.hasNext(); i++)
			retArray[i] = it.next();
		
		return retArray;
	}
	
	protected boolean isNeighbor(Node node1, Node node2)
	{
		if (node1.getPosition().distance(node2.getPosition()) < effectiveRadius)
			return true;
		else
			return false;
	}
	
	protected boolean contains(Node[] list, Node node)
	{
		
		for (Node aNode : list)
			if (aNode == node) return true;
		return false;
	}

	protected Node[] getNeighbors(Node[] allNodes) {
		ArrayList<Node> n = new ArrayList<Node>();
		for (int i = 0; i < allNodes.length; i++) {
			if (this.getPhantomNode().getPosition().distance(allNodes[i].getPosition()) < effectiveRadius) {
				n.add(allNodes[i]);
			}
		}
		Node[] retArray = new Node[n.size()];
		for (int i = 0; i < n.size(); i++) retArray[i] = n.get(i);
		return retArray;
	}

	protected void calculateMinMax(Node[] allNodes) {
		this.limitMax = -999999;
		this.limitMin = 999999;
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getNode().getPosition().x > limitMax)
				limitMax = allNodes[i].getNode().getPosition().x;
			if (allNodes[i].getNode().getPosition().x < limitMin)
				limitMin = allNodes[i].getNode().getPosition().x;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wilmascope.util.Plugin#getControls()
	 */
	public JPanel getControls() {
		// TODO Auto-generated method stub
		return controlPanel;
	}
	
	protected void msg(String m)
	{
		System.out.println(m);
	}

}
