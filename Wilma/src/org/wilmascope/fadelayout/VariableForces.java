/*
 * Created on Jun 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.wilmascope.fadelayout;

/**
 * @author cmurray
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class VariableForces {
	
	public static final int none = 0;
	public static final int levelConstraint = 1;
	public static final int degree = 2;
	public static final int spherelevelConstraint = 3;
	public static final int sphereDegree = 4;
	
	public static final int defaultColouring = 0;
	public static final int levelConstraintColouring = 1;
	public static final int degreeColouring = 2;
	
	public static boolean edgeForce = false;
	public static float edgeResilience = 0.3f;
	public static boolean edgeWeights = true;
	
	public static boolean nonEdgeForce = false;
	public static float repelIntensity = 0.25f;
	public static float accuracyParameter = 0.7f;
	public static String openingCriterion = new String("bh");
	
	public static boolean originForce = false;
	public static float originIntensity = 0.1f;
	
	public static boolean repellingEdges = false;
	public static boolean inOnly = true;
	public static int layering = none;
	
	public static int colouring = defaultColouring;
}
