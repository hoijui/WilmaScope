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
package org.wilmascope.file;

/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3f;

import org.wilmascope.control.GraphControl;
import org.wilmascope.forcelayout.Force;
import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.forcelayout.ForceManager;
import org.wilmascope.view.EdgeView;
import org.wilmascope.view.NodeView;
import org.wilmascope.view.ViewManager;

class GraphFileFilter extends FileFilter {
  public boolean accept(java.io.File file) {
    return file.getName().endsWith(".xwg");
  }
  public String getDescription() {
    return "XML Wilma Graph data files (.xwg)";
  }
}
public class FileHandler {
  GraphControl graphControl;
  Hashtable nodeLookup;
  Hashtable idLookup;
  public FileHandler(GraphControl graphControl) {
    this.graphControl = graphControl;
  }
  public void load(String fileName) {
    XMLGraph xmlGraph = new XMLGraph(fileName);
    try {
      xmlGraph.load();
    } catch(java.io.IOException ex) {
      ex.printStackTrace();
    }
    nodeLookup = new Hashtable();
    graphControl.getRootCluster().removeAllForces();
    graphControl.freeze();
    long startTime = System.currentTimeMillis();
    loadCluster(xmlGraph.getRootCluster(),graphControl.getRootCluster());
    long endTime = System.currentTimeMillis();
    long time = endTime - startTime;
    System.out.println("Loaded... in milliseconds: "+time);
    graphControl.unfreeze();
  }
  public FileFilter getFileFilter() {
    return new GraphFileFilter();
  }
  private void loadEdgeProperties(XMLGraph.Edge xe, GraphControl.EdgeFacade ge) {
    XMLGraph.ViewType viewType = xe.getViewType();
    if(viewType != null) {
      try {
        EdgeView v = ViewManager.getInstance().createEdgeView(viewType.getName());
        ge.setView(v);
        v.setProperties(viewType.getProperties());
      } catch(ViewManager.UnknownViewTypeException e) {
        e.printStackTrace();
      }
    }

    Properties p = xe.getProperties();
    if(p != null) {
      String weight = p.getProperty("Weight");
      if(weight != null) {
        ge.setWeight(Float.parseFloat(weight));
      }
    }
  }
  private void loadNodeProperties(XMLGraph.Node xn, GraphControl.NodeFacade gn) {
    XMLGraph.ViewType viewType = xn.getViewType();
    if(viewType != null) {
      try{
        NodeView v;
        if(gn instanceof GraphControl.ClusterFacade) {
          v = ViewManager.getInstance().createClusterView(viewType.getName());
          gn.setView(ViewManager.getInstance().createClusterView(viewType.getName()));
        } else {
          v = ViewManager.getInstance().createNodeView(viewType.getName());
        }
        gn.setView(v);
        v.setProperties(viewType.getProperties());
      } catch(ViewManager.UnknownViewTypeException e) {
        e.printStackTrace();
      }
    }

    Properties p = xn.getProperties();
    if(p != null) {
      String position = p.getProperty("Position");
      if(position != null) {
        StringTokenizer st = new StringTokenizer(position);
        gn.setPosition(new Point3f(
          Float.parseFloat(st.nextToken()),
          Float.parseFloat(st.nextToken()),
          Float.parseFloat(st.nextToken())));
      }
      String levelConstraint = p.getProperty("LevelConstraint");
      if(levelConstraint != null) {
        gn.setLevelConstraint(Integer.parseInt(levelConstraint));
      }
    }
  }
  private void saveEdgeProperties(GraphControl.EdgeFacade ge,XMLGraph.Edge xe) {
    EdgeView v = (EdgeView)ge.getView();
    if(ge.getView()!=null) {
      XMLGraph.ViewType xv = xe.setViewType(v.getTypeName());
      xv.setProperties(v.getProperties());
    }
    Properties p = new Properties();
  }
  private void saveNodeProperties(GraphControl.NodeFacade gn,XMLGraph.Node xn) {
    NodeView v = (NodeView)gn.getView();
    if(gn.getView()!=null) {
      XMLGraph.ViewType xv = xn.setViewType(v.getTypeName());
      xv.setProperties(v.getProperties());
    }
    Properties p = new Properties();
    String data;
    /*
    if((data = graphNode.getData())!=null && data.length()!=0) {
      xmlNode.setData(data);
    }
    */
    Point3f pos = gn.getPosition();
    p.setProperty("Position",pos.x+" "+pos.y+" "+pos.z);
    if(gn.getLevelConstraint() != Integer.MIN_VALUE) {
      p.setProperty("LevelConstraint",""+gn.getLevelConstraint());
    }
    xn.setProperties(p);
  }
  private void loadCluster(
    XMLGraph.Cluster xmlRoot,
    GraphControl.ClusterFacade graphRoot
  ) {
    Vector nodes=new Vector(), edges=new Vector(),
      forces=new Vector(), clusters=new Vector();
    xmlRoot.load(nodes,edges,clusters);
    XMLGraph.LayoutEngineType layoutEngine = xmlRoot.getLayoutEngineType();
    loadLayoutEngine(layoutEngine,graphRoot);
    loadNodeProperties(xmlRoot, graphRoot);
    for(int i=0;i<nodes.size();i++) {
      XMLGraph.Node xmlNode = (XMLGraph.Node)nodes.get(i);
      GraphControl.NodeFacade n = graphRoot.addNode();
      loadNodeProperties(xmlNode, n);
      nodeLookup.put(xmlNode.getID(),n);
    }
    for(int i=0;i<clusters.size(); i++) {
      XMLGraph.Cluster xc = (XMLGraph.Cluster)clusters.get(i);
      GraphControl.ClusterFacade gc = graphRoot.addCluster();
      loadCluster(xc, gc);
    }
    for(int i=0;i<edges.size();i++) {
      XMLGraph.Edge xmlEdge = (XMLGraph.Edge)edges.get(i);
      GraphControl.EdgeFacade e = graphRoot.addEdge(
        (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getStartID()),
        (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getEndID()));
      loadEdgeProperties(xmlEdge,e);
    }
  }
  private void loadLayoutEngine(XMLGraph.LayoutEngineType l, GraphControl.ClusterFacade c) {
    String type = l.getName();
    if(type.equals("ForceLayout")) {
      Properties p = l.getProperties();
      ForceLayout fl = (ForceLayout)c.getLayoutEngine();
      ForceManager m = ForceManager.getInstance();
      for(Enumeration k = p.keys(); k.hasMoreElements();) {
        String forceName = (String)k.nextElement();
        float strength = Float.parseFloat(p.getProperty(forceName));
        if(forceName.equals("VelocityAttenuation")) {
          fl.setVelocityAttenuation(strength);
        }
        else {
        try{
            Force f = m.createForce(forceName);
            f.setStrengthConstant(strength);
            fl.addForce(f);
          } catch(ForceManager.UnknownForceTypeException e) {
            e.printStackTrace();
          }
        }
      }

    } else if(type.equals("DotLayout")) {
      org.wilmascope.dotlayout.DotLayout dl = new org.wilmascope.dotlayout.DotLayout(c.getCluster());
      c.setLayoutEngine(dl);
    } else if(type.equals("ColumnLayout")) {
      org.wilmascope.columnlayout.ColumnLayout cl = new org.wilmascope.columnlayout.ColumnLayout(c.getCluster());
      c.setLayoutEngine(cl);
      Properties p = l.getProperties();
      cl.setBaseStratum(Integer.parseInt(p.getProperty("BaseLevel")));
    }
  }
  public void save(String fileName) {
    if(!fileName.endsWith(".xwg")) {
      fileName = fileName.concat(".xwg");
      System.out.println("File: "+fileName);
    }
    XMLGraph xmlGraph = new XMLGraph(fileName);
    xmlGraph.create();
    idLookup = new Hashtable();
    saveCluster(graphControl.getRootCluster(),xmlGraph.getRootCluster());
    xmlGraph.save();
  }
  private void saveCluster(
    GraphControl.ClusterFacade graphCluster,
    XMLGraph.Cluster xmlCluster
  ) {
    saveLayoutEngine(graphCluster,xmlCluster);
    GraphControl.NodeFacade[] nodes = graphCluster.getNodes();
    Hashtable clusters = new Hashtable();
    for(int i=0; i<nodes.length; i++) {
      GraphControl.NodeFacade graphNode = nodes[i];
      XMLGraph.Node xmlNode;
      if(graphNode instanceof GraphControl.ClusterFacade) {
        xmlNode = xmlCluster.addCluster();
        clusters.put(graphNode, xmlNode);
      } else {
        xmlNode = xmlCluster.addNode();
        idLookup.put(graphNode, xmlNode.getID());
      }
      saveNodeProperties(graphNode, xmlNode);
    }
    for(Enumeration e = clusters.keys(); e.hasMoreElements();) {
      GraphControl.ClusterFacade graphChildCluster
        = (GraphControl.ClusterFacade)e.nextElement();
      saveCluster(graphChildCluster, (XMLGraph.Cluster)clusters.get(graphChildCluster));
    }
    GraphControl.EdgeFacade[] edges = graphCluster.getEdges();
    for(int i=0; i<edges.length; i++) {
      XMLGraph.Edge xmlEdge = xmlCluster.addEdge(
          (String)idLookup.get(edges[i].getStartNode()),
          (String)idLookup.get(edges[i].getEndNode()));
      saveEdgeProperties(edges[i], xmlEdge);
    }
  }
  private void saveLayoutEngine(
      GraphControl.ClusterFacade graphRoot, XMLGraph.Cluster xmlCluster) {
    XMLGraph.LayoutEngineType l = null;
    Properties p = new Properties();
    org.wilmascope.graph.LayoutEngine gl = graphRoot.getLayoutEngine();
    if(gl instanceof org.wilmascope.forcelayout.ForceLayout) {
      l = xmlCluster.setLayoutEngineType("ForceLayout");
      org.wilmascope.forcelayout.ForceLayout fl = (org.wilmascope.forcelayout.ForceLayout)gl;
      Vector forces = fl.getForces();
      for(Iterator i=forces.iterator(); i.hasNext();) {
        org.wilmascope.forcelayout.Force f = (org.wilmascope.forcelayout.Force)i.next();
        p.setProperty(f.getTypeName(),""+f.getStrengthConstant());
      }
      p.setProperty("VelocityAttenuation",""+fl.getVelocityAttenuation());
      l.setProperties(p);
    } else if(gl instanceof org.wilmascope.dotlayout.DotLayout) {
      l = xmlCluster.setLayoutEngineType("DotLayout");
    } else if(gl instanceof org.wilmascope.columnlayout.ColumnLayout) {
      l = xmlCluster.setLayoutEngineType("ColumnLayout");
      org.wilmascope.columnlayout.ColumnLayout cl = (org.wilmascope.columnlayout.ColumnLayout)gl;
      p.setProperty("BaseLevel",""+cl.getBaseStratum());
      l.setProperties(p);
    }
  }
}
