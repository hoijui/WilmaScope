package org.wilmascope.viewplugin;

/**
 * @author dwyer
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface TubeView {
  public void setEndRadii(final float bottomRadius, final float topRadius);
  public void setHeight(final float height);
  public float getBottomRadius();
  public float getTopRadius();
}
