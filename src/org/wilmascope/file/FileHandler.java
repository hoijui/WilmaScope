/*
 * The following source code is part of the WilmaScope 3D Graph Drawing Engine
 * which is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 * 
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 * 
 * Copyright for this work is retained by Tim Dwyer and the WilmaScope
 * organisation (www.wilmascope.org) however it may be used or modified to work
 * as part of other software subject to the terms of the LGPL. I only ask that
 * you cite WilmaScope as an influence and inform us (tgdwyer@yahoo.com) if you
 * do anything really cool with it.
 * 
 * The WilmaScope software source repository is hosted by Source Forge:
 * www.sourceforge.net/projects/wilma -- Tim Dwyer, 2001
 */
package org.wilmascope.file;
/**
 * Handles reading and writing of graph data structures to files
 * 
 * @author Tim Dwyer
 * @version 1.0
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3f;

import org.wilmascope.control.GraphControl;
import org.wilmascope.degreelayout.DegreeLayout;
import org.wilmascope.fadelayout.FadeLayout;
import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.gmlparser.GMLLoader;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.highdimensionlayout.HighDimensionLayout;
import org.wilmascope.multiscalelayout.MultiScaleLayout;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.NodeView;
import org.wilmascope.view.ViewManager;
class GraphFileFilter extends FileFilter {
	public boolean accept(java.io.File file) {
		return file.isDirectory() || file.getName().endsWith(".xwg")
				|| file.getName().endsWith(".gml");
	}
	public String getDescription() {
		return "XML Wilma Graph data files (.xwg) or common graph (.gml)";
	}
}
class JPEGFileFilter extends FileFilter {
  public boolean accept(java.io.File file) {
    return file.isDirectory() || file.getName().endsWith(".jpg");
  }
  public String getDescription() {
    return "JPEG Image Files (.jpg)";
  }
}
public class FileHandler {
	GraphControl graphControl;
	Hashtable nodeLookup;
	Hashtable idLookup;
	boolean needsLayout = true;
	public FileHandler(GraphControl graphControl) {
		this.graphControl = graphControl;
	}
	public void load(String fileName) {
		if (fileName.endsWith(".xwg")) {
			XMLGraph xmlGraph = new XMLGraph(fileName);
			try {
				xmlGraph.load();
			} catch (java.io.IOException ex) {
				ex.printStackTrace();
			}
			nodeLookup = new Hashtable();
			//graphControl.getRootCluster().removeAllForces();
			graphControl.freeze();
			long startTime = System.currentTimeMillis();
			loadCluster(xmlGraph.getRootCluster(), graphControl
					.getRootCluster());
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			System.out.println("Loaded... in milliseconds: " + time);
			graphControl.getRootCluster().getCluster().draw();
			if (needsLayout) {
				graphControl.unfreeze();
			}
		} else if (fileName.endsWith(".gml")) {
			GMLLoader gmlLoader = new GMLLoader(graphControl, fileName);
		}
	}
  public static FileFilter getFileFilter() {
    return new GraphFileFilter();
  }
  public static FileFilter getJPEGFileFilter() {
    return new JPEGFileFilter();
  }
	private void loadEdgeProperties(XMLGraph.Edge xe, GraphControl.EdgeFacade ge) {
		XMLGraph.ViewType viewType = xe.getViewType();
		if (viewType != null) {
			try {
        String viewTypeString = viewType.getName();
				EdgeView v = ViewManager.getInstance().createEdgeView(
						viewTypeString);
				ge.setView(v);
				v.setProperties(viewType.getProperties());
        {
          Point3f s = ge.getStartNode().getPosition();
          Point3f e = ge.getEndNode().getPosition();
          if(s.z<e.z) {
            System.out.println("Backward Edge, type="+viewTypeString+", startLabel="+ge.getStartNode().getLabel()+", endLabel="+ge.getEndNode().getLabel());
          }
        }
			} catch (ViewManager.UnknownViewTypeException e) {
				e.printStackTrace();
			}
		}
		Properties p = xe.getProperties();
		if (p != null) {
			String weight = p.getProperty("Weight");
			if (weight != null) {
				ge.setWeight(Float.parseFloat(weight));
			}
		}
	}
	private void loadNodeProperties(XMLGraph.Node xn, GraphControl.NodeFacade gn) {
		XMLGraph.ViewType viewType = xn.getViewType();
		if (viewType != null) {
			try {
				NodeView v;
				if (gn instanceof GraphControl.ClusterFacade) {
					v = ViewManager.getInstance().createClusterView(
							viewType.getName());
					gn.setView(ViewManager.getInstance().createClusterView(
							viewType.getName()));
				} else {
					v = ViewManager.getInstance().createNodeView(
							viewType.getName());
				}
				gn.setView(v);
				v.setProperties(viewType.getProperties());
			} catch (ViewManager.UnknownViewTypeException e) {
				e.printStackTrace();
			}
		}
		Properties p = xn.getProperties();
		if (p != null) {
			String position = p.getProperty("Position");
			if (position != null) {
				StringTokenizer st = new StringTokenizer(position);
				gn.setPosition(new Point3f(Float.parseFloat(st.nextToken()),
						Float.parseFloat(st.nextToken()), Float.parseFloat(st
								.nextToken())));
			}
			String levelConstraint = p.getProperty("LevelConstraint");
			String fixedPosition = p.getProperty("FixedPosition");
			if (levelConstraint != null) {
				gn.setLevelConstraint(Integer.parseInt(levelConstraint));
			}
			if (fixedPosition != null) {
				gn.setFixedPosition(true);
			}
		}
	}
	private void saveEdgeProperties(GraphControl.EdgeFacade ge, XMLGraph.Edge xe) {
		EdgeView v = (EdgeView) ge.getView();
		if (ge.getView() != null) {
			XMLGraph.ViewType xv = xe.setViewType(v.getTypeName());
			xv.setProperties(v.getProperties());
		}
		Properties p = new Properties();
		p.setProperty("Weight", "" + ge.getWeight());
		xe.setProperties(p);
	}
	private void saveNodeProperties(GraphControl.NodeFacade gn, XMLGraph.Node xn) {
		NodeView v = (NodeView) gn.getView();
		if (gn.getView() != null) {
			XMLGraph.ViewType xv = xn.setViewType(v.getTypeName());
			xv.setProperties(v.getProperties());
		}
		Properties p = new Properties();
		String data;
		/*
		 * if((data = graphNode.getData())!=null && data.length()!=0) {
		 * xmlNode.setData(data); }
		 */
		Point3f pos = gn.getPosition();
		p.setProperty("Position", pos.x + " " + pos.y + " " + pos.z);
		if (gn.getLevelConstraint() != Integer.MIN_VALUE) {
			p.setProperty("LevelConstraint", "" + gn.getLevelConstraint());
		}
		if (gn.isFixedPosition()) {
			p.setProperty("FixedPosition", "True");
		}
		xn.setProperties(p);
	}
	private void loadCluster(XMLGraph.Cluster xmlRoot,
			GraphControl.ClusterFacade graphRoot) {
		Vector nodes = new Vector(), edges = new Vector(), forces = new Vector(), clusters = new Vector();
		xmlRoot.load(nodes, edges, clusters);
		XMLGraph.LayoutEngineType layoutEngine = xmlRoot.getLayoutEngineType();
		loadLayoutEngine(layoutEngine, graphRoot);
		loadNodeProperties(xmlRoot, graphRoot);
		for (int i = 0; i < nodes.size(); i++) {
			XMLGraph.Node xmlNode = (XMLGraph.Node) nodes.get(i);
			GraphControl.NodeFacade n = graphRoot.addNode();
			loadNodeProperties(xmlNode, n);
			nodeLookup.put(xmlNode.getID(), n);
		}
		for (int i = 0; i < clusters.size(); i++) {
			XMLGraph.Cluster xc = (XMLGraph.Cluster) clusters.get(i);
			GraphControl.ClusterFacade gc = graphRoot.addCluster();
			loadCluster(xc, gc);
		}
		for (int i = 0; i < edges.size(); i++) {
			XMLGraph.Edge xmlEdge = (XMLGraph.Edge) edges.get(i);
			GraphControl.EdgeFacade e = graphRoot.addEdge(
					(GraphControl.NodeFacade) nodeLookup.get(xmlEdge
							.getStartID()),
					(GraphControl.NodeFacade) nodeLookup
							.get(xmlEdge.getEndID()));
			loadEdgeProperties(xmlEdge, e);
		}
	}
	private void loadLayoutEngine(XMLGraph.LayoutEngineType l,
			GraphControl.ClusterFacade c) {
		String type = l.getName();
		LayoutEngine e = null;
		if (type.equals("ForceLayout")) {
			e = new ForceLayout();
		} else if (type.equals("DotLayout")) {
			e = new org.wilmascope.dotlayout.DotLayout();
			needsLayout = true;
		} else if (type.equals("ColumnLayout")) {
			e = new org.wilmascope.columnlayout.ColumnLayout();
		} else if (type.equals("MultiScaleLayout")) {
			e = new MultiScaleLayout();
		} else if (type.equals("FadeLayout")) {
			e = new FadeLayout();
		} else if (type.equals("HighDimensionLayout")) {
			e = new HighDimensionLayout();
		} else if (type.equals("DegreeLayout")) {
			e = new DegreeLayout();
		}
		c.setLayoutEngine(e);
		e.setProperties(l.getProperties());
	}
	public void save(String fileName) {
		if (!fileName.endsWith(".xwg")) {
			fileName = fileName.concat(".xwg");
			System.out.println("File: " + fileName);
		}
		XMLGraph xmlGraph = new XMLGraph(fileName);
		xmlGraph.create();
		idLookup = new Hashtable();
		saveCluster(graphControl.getRootCluster(), xmlGraph.getRootCluster());
		xmlGraph.save();
	}
	private void saveCluster(GraphControl.ClusterFacade graphCluster,
			XMLGraph.Cluster xmlCluster) {
		saveLayoutEngine(graphCluster, xmlCluster);
		GraphControl.NodeFacade[] nodes = graphCluster.getNodes();
		Hashtable clusters = new Hashtable();
		for (int i = 0; i < nodes.length; i++) {
			GraphControl.NodeFacade graphNode = nodes[i];
			XMLGraph.Node xmlNode;
			if (graphNode instanceof GraphControl.ClusterFacade) {
				xmlNode = xmlCluster.addCluster();
				clusters.put(graphNode, xmlNode);
			} else {
				xmlNode = xmlCluster.addNode();
				idLookup.put(graphNode, xmlNode.getID());
			}
			saveNodeProperties(graphNode, xmlNode);
		}
		for (Enumeration e = clusters.keys(); e.hasMoreElements();) {
			GraphControl.ClusterFacade graphChildCluster = (GraphControl.ClusterFacade) e
					.nextElement();
			saveCluster(graphChildCluster, (XMLGraph.Cluster) clusters
					.get(graphChildCluster));
		}
		GraphControl.EdgeFacade[] edges = graphCluster.getEdges();
		for (int i = 0; i < edges.length; i++) {
			XMLGraph.Edge xmlEdge = xmlCluster.addEdge((String) idLookup
					.get(edges[i].getStartNode()), (String) idLookup
					.get(edges[i].getEndNode()));
			saveEdgeProperties(edges[i], xmlEdge);
		}
	}
	private void saveLayoutEngine(GraphControl.ClusterFacade graphRoot,
			XMLGraph.Cluster xmlCluster) {
		XMLGraph.LayoutEngineType l = null;
		Properties p = new Properties();
		org.wilmascope.graph.LayoutEngine gl = graphRoot.getLayoutEngine();
		if (gl instanceof org.wilmascope.forcelayout.ForceLayout) {
			l = xmlCluster.setLayoutEngineType("ForceLayout");
		} else if (gl instanceof org.wilmascope.dotlayout.DotLayout) {
			l = xmlCluster.setLayoutEngineType("DotLayout");
		} else if (gl instanceof org.wilmascope.columnlayout.ColumnLayout) {
			l = xmlCluster.setLayoutEngineType("ColumnLayout");
		} else if (gl instanceof MultiScaleLayout) {
			l = xmlCluster.setLayoutEngineType("MultiScaleLayout");
		} else if (gl instanceof FadeLayout) {
			l = xmlCluster.setLayoutEngineType("FadeLayout");
		} else if (gl instanceof HighDimensionLayout) {
			l = xmlCluster.setLayoutEngineType("HighDimensionLayout");
		} else if (gl instanceof DegreeLayout) {
			l = xmlCluster.setLayoutEngineType("DegreeLayout");
		}
		l.setProperties(gl.getProperties());
	}
}