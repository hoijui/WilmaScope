package org.wilmascope.dotlayout;

/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */
import java.util.Vector;
import java.awt.Rectangle;

public interface Spline {
  public void setScale(float xScale, float yScale);
  public void setCurves(float xScale, float yScale, int x0, int y0, int x1, int y1, Vector curves, Vector arrowPositions);
  public Vector getCurves();
  public Vector getArrowPositions();
  public float getXScale();
  public float getYScale();
  public Rectangle getBounds();
  public void copyCurves(Spline original);
}
