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

/**
 * A GraphElement is the basic ancestor of anything that can appear in graph
 * be it a Node, Edge or Cluster.
 */
public abstract class GraphElement {
  public GraphElement() {
  }
  /**
   * add this element to a particular owner cluster
   * @param c the new owner
   */
  public void setOwner(Cluster c) {
    if(owner != null) {
      owner.remove(this);
    }
    owner = c;
  }
  /**
   * get the parent cluster or owner of this graph element
   */
  public Cluster getOwner() {
    return owner;
  }
  /**
   * draw the element if it's visible
   */
  public void draw() {
    if(visible) view.draw();
  }
  public boolean isVisible() {
    return visible;
  }
  public void hide() {
    if(visible && view!=null) {
      visible = false;
      view.hide();
    }
  }
  public void show(org.wilmascope.view.GraphCanvas graphCanvas) {
    if(!visible && view!=null) {
      visible = true;
      view.show(graphCanvas);
    }
  }
  public void setUserFacade(Object userFacade) {
    this.userFacade = userFacade;
  }
  public Object getUserFacade() {
    return userFacade;
  }
  /**
   * delete the element, removing all references to it, forever!
   */
  public abstract void delete();
  /**
   * Any properties loaded or saved in files con be stored
   * in a Properties object.  This method is called to extract the
   * GraphElement's properties when the
   * file is created.
   * @return Custom properties associated with this GraphElement.
   */
  public abstract Properties getProperties();
  public abstract void setProperties(Properties p);
  protected Viewable view;
  protected Layable layout;
  protected boolean visible = false;
  protected Cluster owner;
  protected Object userFacade;
}
