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

public class EdgeList extends List<Edge> {
  public EdgeList(EdgeList edgeList) {
    addAll(edgeList);
  }
  public EdgeList(java.util.Set<Edge> edgeSet) {
    elements.addAll(edgeSet);
  }
  public void addAll(EdgeList edgeList) {
    elements.addAll(edgeList.getElementsVector());
  }
  public void setEdges(EdgeList edges) {
    elements.clear();
    elements.addAll(edges.getElementsVector());
  }
  public EdgeList() {}
  public void add(Edge e) {
    if(!elements.contains(e)) {
      elements.add(e);
    }
  }
  public void remove(Edge e) {
    elements.remove(e);
  }
  public Edge get(int index) {
    return (Edge)elements.get(index);
  }
  public boolean contains(Edge edge) {
    return elements.contains(edge);
  }
}
