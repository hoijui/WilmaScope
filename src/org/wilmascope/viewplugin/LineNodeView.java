package org.wilmascope.viewplugin;

import org.wilmascope.view.*;
import javax.vecmath.Color3f;
import org.wilmascope.graph.EdgeList;
import org.wilmascope.graph.Edge;
import org.wilmascope.graph.Node;
import javax.swing.ImageIcon;

/**
 * Basically the following is an invisible dummy node...
 * might replace it with an OpenGL dot
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
  public ImageIcon getIcon() {
    return new ImageIcon(org.wilmascope.images.Images.class.getResource("dot.png"));
  }
}
