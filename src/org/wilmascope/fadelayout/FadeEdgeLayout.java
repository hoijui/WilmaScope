/*
 * Created on May 18, 2004
 *
 */
package org.wilmascope.fadelayout;

import org.wilmascope.graph.EdgeLayout;

/**
 * @author cmurray
 *
 */
public class FadeEdgeLayout extends EdgeLayout {
	private final float defaultEdgeLength = 0.75f;
	//private final float defaultEdgeResilience = 1.0f;
	
	public float idealLength = defaultEdgeLength;
	//public float resilience = defaultEdgeResilience;
	
	//public int weight;
	
	/*public float getIdealLength()
	{
		if (VariableForces.edgeWeights)
		{
			if (getEdge().getWeight() != 0f)
			{
				return Math.max(idealLength, (20f*idealLength)/(getEdge().getWeight()));
			}
			else
			{
				return 20f;
			}
		}
		else
		{
			return idealLength;
		}
	}*/
}
