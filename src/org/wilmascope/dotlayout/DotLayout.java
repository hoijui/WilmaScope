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

package org.wilmascope.dotlayout;

import org.wilmascope.graph.*;
import org.wilmascope.dotparser.*;
import org.wilmascope.global.Constants;
import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.awt.geom.Point2D;
import javax.vecmath.Point3f;

/**
 * <p>A layout engine which uses the dot program to find node and edge positions.
 * Dot is part of the AT&T Labs-Research
 * <a href="http://www.research.att.com/sw/tools/graphviz/">Graphviz suite</a>.
 * </p>
 * <p>Copyright: Copyright (c) Tim Dwyer 2001</p>
 * <p>Company: WilmaScope.org</p>
 * @author Tim Dwyer
 * @version 1.0
 */

public class DotLayout implements LayoutEngine {
  public DotLayout(Cluster root) {
    this.root = root;
  }

  TreeMap nodeLookup = new TreeMap();
  TreeMap edgeLookup = new TreeMap();
  TreeMap layerLookup = new TreeMap();
  TreeMap curveLookup = new TreeMap();
  Vector curvelessEdges = new Vector();

  public void calculateLayout() {
    NodeList nodes = root.getNodes();

    try {
      String dotPath = Constants.getInstance().getProperty("DotPath");
      FileOutputStream in = new FileOutputStream(new File("in.dot"));
      in.write(
        "digraph d { graph [concentrate=true]; node [shape=circle ".getBytes());
      in.write("fixedsize=true ".getBytes());
      in.write("layer=all ".getBytes());
      in.write("];\n".getBytes());
      in.write("layers=\"l0:l1:l2:l3:l4\"\n".getBytes());
      for (nodes.resetIterator(); nodes.hasNext();) {
        Node n = nodes.nextNode();
        String id = "" + n.hashCode();
        nodeLookup.put(id, n);
        String text =
          id
            + " [label=\""
            + ((org.wilmascope.view.GraphElementView) n.getView()).getLabel()
            + "\" shape=circle width=\"1\" height=\"1\"];\n";
        in.write(text.getBytes());
      }
      EdgeList edges = root.getInternalEdges();

      for (edges.resetIterator(); edges.hasNext();) {
        Edge e = edges.nextEdge();
        Node start = e.getStart();
        Node end = e.getEnd();
        DotEdgeLayout eLayout = (DotEdgeLayout) e.getLayout();
        eLayout.setZLevel(
          ((org.wilmascope.columnlayout.NodeColumnLayout) start.getLayout())
            .getLevel());
        if (start.getOwner() != root) {
          start = start.getOwner();
        }
        if (end.getOwner() != root) {
          end = end.getOwner();
        }
        String text = new String(start.hashCode() + "->" + end.hashCode());
        String layerText = new String("l"+eLayout.getZLevel());
        edgeLookup.put(text + "(" + layerText + ")", e);
        in.write(
          text.concat("[ layer=\"" + layerText + "\" ").getBytes());
        Vector layerList =
          (Vector) layerLookup.get(new Integer(eLayout.getZLevel()));
        if (layerList == null) {
          layerList = new Vector();
          layerLookup.put(new Integer(eLayout.getZLevel()), layerList);
        }
        layerList.add(text);
        in.write(("weight=\"" + ((int) e.getWeight() + 1) + "\" ").getBytes());
        in.write("]".getBytes());
        in.write(";\n".getBytes());
      }
      if (stratified) {
        for (int i = 0; i < (layerLookup.size() - 1); i++) {
          String t = "dummy" + i + "->dummy" + (int) (i + 1) + "[minlen=3];\n";
          in.write(t.getBytes());
        }
        for (nodes.resetIterator(); nodes.hasNext();) {
          Node n = nodes.nextNode();
          String id = "" + n.hashCode();
          EdgeList es = n.getEdges();
          float aveEdgeLevel = 0;
          for (es.resetIterator(); es.hasNext();) {
            aveEdgeLevel
              += (float) ((DotEdgeLayout) es.nextEdge().getLayout()).getZLevel();
          }
          aveEdgeLevel /= (float) es.size();
          int dummyLevel = (int) Math.floor((double) aveEdgeLevel);
          String text = "dummy" + dummyLevel + "->" + n.hashCode() + ";\n";
          in.write(text.getBytes());
          text = n.hashCode() + "->" + "dummy" + (dummyLevel + 1) + ";\n";
          in.write(text.getBytes());
        }
      }
      in.write("}\n".getBytes());
      in.flush();
      in.close();
      Process p = Runtime.getRuntime().exec(dotPath + " in.dot -o out.dot");
      InputStream err = p.getErrorStream();
      try {
        BufferedReader r = new BufferedReader(new InputStreamReader(err));
        String s;
        while ((s = r.readLine()) != null) {
          System.out.println(s);
        }
        parseDot(new FileInputStream("out.dot"));
      } catch (org.wilmascope.dotparser.ParseException e) {
        System.err.println(e.getMessage());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  static DotParser parser;
  private boolean stratified = false;
  /**
   * parses a dot output file
   */
  void parseDot(InputStream stream)
    throws org.wilmascope.dotparser.ParseException {
    InputStream f = new LineBreakFilter(stream);
    if (parser == null) {
      parser = new DotParser(f);
    } else {
      parser.ReInit(f);
    }
    parser.graph(new GraphClient() {
      public void setBoundingBox(int x0, int y0, int x1, int y1) {
        bbXMin = x0;
        bbYMin = y0;
        bbXMax = x1;
        bbYMax = y1;
        width = (float) (bbXMax - bbXMin);
        height = (float) (bbYMax - bbYMin);
      }
      public EdgeClient addEdge(NodeClient start, NodeClient end) {
        return new EdgeClient(start, end) {
          public void setCurves(Vector curves) {
            String start = this.start.getID();
            String end = this.end.getID();
            String key = new String(start + "->" + end);
            if (curves.size() != 0) {
              curveLookup.put(key, curves);
            }
            if (layer != null) {
              key = key.concat("(" + layer + ")");
            }
            Edge e = (Edge) edgeLookup.get(key);
            if (e == null) {
              System.err.println(
                "Warning: null edge (OK if it's a dummy edge)");
              return;
            }
            if (curves.size() == 0) {
              curvelessEdges.add(e);
              return;
            }
            Spline spline = (Spline)e.getView();
            spline.setCurves(
              scale,
              bbXMin,
              bbYMin,
              bbXMax,
              bbYMax,
              curves,
              getArrowPositions());
          }
        };
      }
      public NodeClient addNode(String id) {
        NodeClient n = (NodeClient) nodeList.get(id);
        if (n == null) {
          n = new NodeClient(id) {
            public void setPosition(int x, int y) {
              Node n = (Node) nodeLookup.get(this.id);
              if (n == null) {
                return;
              }
              Point3f p = n.getPosition();
              p.x = scale * ((float) (x - bbXMin) / width - 0.5f);
              p.y = scale * ((float) (y - bbYMin) / width - 0.5f);
            }
          };
          nodeList.put(id, n);
        }
        return n;
      }
      Hashtable nodeList = new Hashtable();
    });
    /*
    for (java.util.Iterator i = curvelessEdges.iterator(); i.hasNext();) {
      Edge e = (Edge) i.next();
      Node start = e.getStart();
      Node end = e.getEnd();
      if (start.getOwner() != root) {
        start = start.getOwner();
      }
      if (end.getOwner() != root) {
        end = end.getOwner();
      }
      String key = new String(start.hashCode() + "->" + end.hashCode());
      Vector curves = (Vector) curveLookup.get(key);
      if(curves == null) {
        key = new String(end.hashCode() + "->" + start.hashCode());
        curves = (Vector) curveLookup.get(key);
      }
      SplineTubeEdgeView spline = (SplineTubeEdgeView) e.getView();
      spline.setCurves(
        scale,
        bbXMin,
        bbYMin,
        bbXMax,
        bbYMax,
        curves,
        new Vector());
    }
    */

  }

  public int bbXMin, bbXMax, bbYMin, bbYMax;
  public float width, height;
  public float scale = 4.0f;

  public boolean applyLayout() {
    return true;
  }

  public void reset() {
  }

  public NodeLayout createNodeLayout(org.wilmascope.graph.Node n) {
    return new DotNodeLayout();
  }

  public EdgeLayout createEdgeLayout(org.wilmascope.graph.Edge e) {
    return new DotEdgeLayout();
  }
  Cluster root;
}
