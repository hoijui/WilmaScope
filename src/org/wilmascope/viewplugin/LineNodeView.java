package org.wilmascope.viewplugin;

import org.wilmascope.view.*;
import javax.vecmath.Color3f;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.Node;
import javax.swing.ImageIcon;

/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */

public class LineNodeView extends NodeView {

  public LineNodeView() {
    setTypeName("LineNode");
  }
  protected void setupHighlightMaterial() {
  }
  protected void setupDefaultMaterial() {
  }
  protected void init() {
  }
  public void setColour(Color3f c) {
    super.setColour(c);
    Node n = getNode();
    this.colour = c;
    EdgeList edges = n.getEdges();
    for(edges.resetIterator();edges.hasNext();) {
      Edge e = edges.nextEdge();
      if(!(e.getView() instanceof LineEdgeView)) {
        System.err.println("WARNING: LineNodeView is only really useful when using LineEdgeViews");
        return;
      }
      LineEdgeView v = (LineEdgeView)e.getView();
      if(e.getStart() == n) {
        v.setStartColour(c);
      } else {
        v.setEndColour(c);
      }
    }
  }
  public Color3f getColor3f() {
    return colour;
  }
  Color3f colour = Colours.white;
  public ImageIcon getIcon() {
    return new ImageIcon(org.wilmascope.images.Images.class.getResource("dot.png"));
  }
}
