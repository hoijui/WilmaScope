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

package org.wilmascope.view;

import org.wilmascope.graph.Node;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import javax.swing.ImageIcon;
/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

public abstract class NodeView extends GraphElementView
implements org.wilmascope.graph.NodeView {

  public NodeView() {
  }
  /*
  public void draw() {
    setTranslation(new Vector3f(node.getPosition()));
  }
  */
  public void draw() {
    double radius = (double)getNode().getRadius();
    setResizeTranslateTransform(
      new Vector3d(radius,radius,radius),
      new Vector3f(getNode().getPosition()));
  }
  public Node getNode() {
    return node;
  }
  public void setNode(Node node) {
    this.node = node;
  }
  public ImageIcon getIcon() {
    return new ImageIcon(getClass().getResource("/images/node.png"));
  }
  private Node node;
}
