/*
 * Created on May 18, 2004
 *
 */
package org.wilmascope.degreelayout;

import org.wilmascope.graph.EdgeLayout;

/**
 * @author cmurray
 *
 */
public class DegreeEdgeLayout extends EdgeLayout {
	private final float defaultEdgeLength = 0.75f;
	//private final float defaultEdgeResilience = 1.0f;
	
	public float idealLength = defaultEdgeLength;
	//public float resilience = defaultEdgeResilience;
}
