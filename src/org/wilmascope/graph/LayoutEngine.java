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

import java.util.Properties;

import javax.swing.JPanel;

/**
 * Classes which determine layout for a particular cluster must implement this
 * interface.
 *
 * @author Tim Dwyer
 * @version $Id$
 *
 */
public interface LayoutEngine<N extends NodeLayout,E extends EdgeLayout> {
  /**
   * calculate the changes required to move the graph to a nicer layout.  This method
   * does not actually update the position of the nodes, rather this should be done by the
   * {@link #applyLayout} method.  Potentially, this means that
   * the caller can calculate new positions (stored in the NodeLayouts), then opt not to
   * apply the changes if it does not lead to an improvement.  
   * In practice this has not yet been utilised and such
   * decisions are usually made internally to the layout engine.
   */
  public void calculateLayout();
  /**
   * apply the changes calculated by
   * {@link #calculateLayout}
   * @return true when a stable state is reached
   */
  public boolean applyLayout();
  /**
   * Return a string descriptor for the layout engine type.  Useful in GUI elements
   * such as comboboxes
   */
  public String getName();
  /**
   * Factory method to create a new NodeLayout implementation compatible with
   * the layout engine implementing this interface.
   */
  public N createNodeLayout(Node n);
  /**
   * Factory method to create a new EdgeLayout implementation compatible with
   * the layout engine implementing this interface.
   */
  public E createEdgeLayout(Edge e);
  public Properties getProperties();
  public void setProperties(Properties p);
  public void resetProperties();
  public JPanel getControls();
  /**
   * The LayoutEngine should have no constructor.  It should be initialised with this method.
   */
  public void init(Cluster root);
}
