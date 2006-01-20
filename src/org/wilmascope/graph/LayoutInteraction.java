package org.wilmascope.graph;

import org.wilmascope.control.GraphControl;
import org.wilmascope.view.GraphCanvas;

/**
 * The class that must be extended to give interactivity to layouts.
 * The class that extends this one must be returned by the 
 * getLayoutInteraction() method of the specific LayoutEngine.
 * 
 * IMPORTANT:  	If you do not want the menu to appear on right click, 
 * 				use storeUserData("divider", "y") for those elements 
 * 
 * @author Tristan Manwaring
 * @version 19/1/06
 */
public abstract class LayoutInteraction {
	//the types of elements where interaction is possible
	public static final int NODE = 0;
	public static final int EDGE = 1;
	public static final int CLUSTER = 2;
	
	private GraphCanvas canvas;
	private GraphControl.Cluster rootCluster;
	
	/**
	 * This method is called from GraphControl in response to a 
	 * left or middle mouse click if the getLayoutInteraction() method 
	 * of the specific LayoutEngine is implemented.
	 *
	 * @param type  the type of element that e is
	 * @param e		the facade that was clicked on
	 * @param gc	the graphcanvas being used
	 * @param root	the root cluster of the graph
	 */
	public void interact(int type, GraphControl.GraphElementFacade e, GraphCanvas gc, GraphControl.Cluster root) {
		canvas = gc;
		rootCluster = root;
		
		//call the specific interaction for each type
		if(type == NODE){
			interact((GraphControl.Node)e);
		} else if(type == EDGE){
			interact((GraphControl.Edge)e);
		} else if(type == CLUSTER){
			interact((GraphControl.Cluster)e);
		}
	}
	
	/**
	 * Must be implemented by inheriting Interaction classes to create interactivity
	 * 
	 * <br><br>IMPORTANT:	Use getUserData("middle mouse picked") == "y" to check
	 * 				if the middle mouse button was clicked, if not then it was
	 * 				the left mouse button. Remember to reset the "middle mouse picked" to "n".
	 * 
	 * @param picked the Node that was clicked
	 */
	protected abstract void interact(final GraphControl.Node picked);

	/**
	 * Must be implemented by inheriting Interaction classes to create interactivity
	 * 
	 * <br><br>IMPORTANT:	Use getUserData("middle mouse picked") == "y" to check
	 * 				if the middle mouse button was clicked, if not then it was
	 * 				the left mouse button. Remember to reset the "middle mouse picked" to "n".
	 * 
	 * @param picked the Edge that was clicked
	 */
	protected abstract void interact(final GraphControl.Edge picked);

	/**
	 * Must be implemented by inheriting Interaction classes to create interactivity
	 * 
	 * <br><br>IMPORTANT:	Use getUserData("middle mouse picked") == "y" to check
	 * 				if the middle mouse button was clicked, if not then it was
	 * 				the left mouse button. Remember to reset the "middle mouse picked" to "n".
	 * 
	 * @param picked the Cluster that was clicked
	 */
	protected abstract void interact(final GraphControl.Cluster picked);
	
	/**
	 * Get the GraphCanvas
	 * @return canvas
	 */
	public GraphCanvas getCanvas(){
		return canvas;
	}
	
	/**
	 * Get the root cluster
	 * @return rootCluster
	 */
	public GraphControl.Cluster getRootCluster(){
		return rootCluster;
	}
}
