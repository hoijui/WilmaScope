/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope organisation
 * (www.wilmascope.org) however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform us (tgdwyer@yahoo.com)
 * if you do anything really cool with it.
 *
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma
 *
 * -- Tim Dwyer, 2001
 */

package org.wilmascope.graph;

/**
 * An interface which a class which determines a layout for a cluster must
 * implement
 * $Id$
 * $Log$
 * Revision 1.6  2002/03/27 07:28:14  tgdwyer
 * Documentation changes
 *
 * Revision 1.5  2002/03/27 07:20:50  tgdwyer
 * Minor changes
 *
 */
public interface LayoutEngine {
  /**
   * calculate the changes required to move the graph to a nicer layout
   */
  public void calculateLayout();
  /**
   * apply the changes calculated by
   * {@link org.wilmascope.graph.LayoutEngine#calculateLayout}
   * @return true when a stable state is reached
   */
  public boolean applyLayout();
  /**
   * Factory method to create a new NodeLayout implementation compatible with
   * the layout engine implementing this interface.
   */
  public NodeLayout createNodeLayout();
  /**
   * Factory method to create a new EdgeLayout implementation compatible with
   * the layout engine implementing this interface.
   */
  public EdgeLayout createEdgeLayout();
}
