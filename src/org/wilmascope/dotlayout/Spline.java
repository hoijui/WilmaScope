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

public interface Spline {
  public void setCurves(float scale, int x0, int y0, int x1, int y1, Vector curves, Vector arrowPositions);
}