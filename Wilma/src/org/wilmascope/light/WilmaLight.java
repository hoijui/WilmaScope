/**
 * @author Christine
 */
package org.wilmascope.light;
import javax.media.j3d.*;

/**
 * The class serves as a memory for the light and it's parent branch group.
 * The light's parent branchgroup cannot be returned using java3d's method once
 * the light is turned 'live'. It's necessary to create a reference for 
 * the light's parent branchgroup so that the light can be deleted after it is turned 'live'.
 */
public class WilmaLight {
	private Light light;
	private BranchGroup lightBranchGroup;
	public Light getLight()
	{
		return light;
	}
	/** @return The parent BranchGroup
	 */
	public BranchGroup getBranchGroup()
	{
		return lightBranchGroup;
	}
	
	public void setLight(Light l)
	{
	   light=l;	
	}
	/** @param Sets the light's parent BranchGroup
	 */
	public void setBranchGroup(BranchGroup b)
	{
		lightBranchGroup=b;
	}

}
