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

import javax.swing.filechooser.FileFilter;
import java.util.*;
import org.wilmascope.control.GraphControl;
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
    graphControl.stall();
    long startTime = System.currentTimeMillis();
    expandXMLCluster(xmlGraph.getRootCluster(),graphControl.getRootCluster());
    long endTime = System.currentTimeMillis();
    long time = endTime - startTime;
    System.out.println("Loaded... in milliseconds: "+time);
    graphControl.unstall();
  }
  public FileFilter getFileFilter() {
    return new GraphFileFilter();
  }
  private void setGraphNodeData(XMLGraph.Node xn, GraphControl.NodeFacade gn) {
    String label = xn.getLabel();
    if(label != null && label.length()!=0) {
      gn.setLabel(label);
    }
    XMLGraph.Colour c = xn.getColour();
    if(c != null) {
      gn.setColour(c.getAWTColour());
    }
    XMLGraph.Position pos = xn.getPosition();
    if(pos != null) {
      gn.setPosition(new javax.vecmath.Point3f(pos.getX(),pos.getY(), pos.getZ()));
    }
    String data;
    if((data = xn.getData())!=null && data.length()!=0) {
      gn.setData(data);
    }
  }
  private void setXMLNodeData(GraphControl.NodeFacade graphNode,XMLGraph.Node xmlNode) {
    String label;
    if((label = graphNode.getLabel())!=null) {
      xmlNode.setLabel(label);
    }
    String data;
    if((data = graphNode.getData())!=null && data.length()!=0) {
      xmlNode.setData(data);
    }
    if(!graphNode.isDefaultColour()) {
      xmlNode.setColour(graphNode.getColour());
    }
    javax.vecmath.Point3f pos = graphNode.getPosition();
    xmlNode.setPosition(pos.x,pos.y,pos.z);
  }
  private void expandXMLCluster(
    XMLGraph.Cluster xmlRoot,
    GraphControl.ClusterFacade graphRoot
  ) {
    Vector nodes=new Vector(), edges=new Vector(),
      forces=new Vector(), clusters=new Vector();
    xmlRoot.getChildren(nodes,edges,forces,clusters);
    for(int i=0;i<nodes.size();i++) {
      XMLGraph.Node xmlNode = (XMLGraph.Node)nodes.get(i);
      String viewType = xmlNode.getViewType();
      GraphControl.NodeFacade n = null;
      if(viewType != null && viewType.length()>0) {
        n = graphRoot.addNode(viewType);
      } else {
        n = graphRoot.addNode();
      }
      setGraphNodeData(xmlNode, n);
      nodeLookup.put(xmlNode.getID(),n);
    }
    for(int i=0;i<forces.size();i++) {
      XMLGraph.Force xmlForce = (XMLGraph.Force)forces.get(i);
      try {
        graphRoot.addForce(xmlForce.getType()).setStrength(xmlForce.getStrength());
      } catch(Exception e) {
        System.out.println("Couldn't add force while loading file because: "+e.getMessage());
      }
    }
    for(int i=0;i<clusters.size(); i++) {
      XMLGraph.Cluster xc = (XMLGraph.Cluster)clusters.get(i);
      String viewType = xc.getViewType();
      GraphControl.ClusterFacade gc = null;
      if(viewType != null && viewType.length()>0) {
        gc = graphRoot.addCluster(viewType);
      } else {
        gc = graphRoot.addCluster();
      }
      setGraphNodeData(xc, gc);
      gc.setRadius(xc.getRadius());
      expandXMLCluster(xc, gc);
    }
    for(int i=0;i<edges.size();i++) {
      XMLGraph.Edge xmlEdge = (XMLGraph.Edge)edges.get(i);
      String viewType;
      if((viewType=xmlEdge.getViewType())!=null && viewType.length()>0) {
        graphRoot.addEdge(
          (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getStartID()),
          (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getEndID()),
          viewType);
      } else {
        graphRoot.addEdge(
          (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getStartID()),
          (GraphControl.NodeFacade)nodeLookup.get(xmlEdge.getEndID()));
      }
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
    createXMLCluster(graphControl.getRootCluster(),xmlGraph.getRootCluster());
    xmlGraph.save();
  }
  private void createXMLCluster(
    GraphControl.ClusterFacade graphCluster,
    XMLGraph.Cluster xmlCluster
  ) {
    GraphControl.ForceFacade[] forces = graphCluster.getForces();
    float radius = graphCluster.getRadius();
    if(radius > 0) {
      xmlCluster.setRadius(radius);
    }
    for(int i=0; i<forces.length; i++) {
      XMLGraph.Force xmlForce = xmlCluster.addForce(
        forces[i].getType(), forces[i].getStrength());
    }
    GraphControl.NodeFacade[] nodes = graphCluster.getNodes();
    Hashtable clusters = new Hashtable();
    for(int i=0; i<nodes.length; i++) {
      GraphControl.NodeFacade graphNode = nodes[i];
      XMLGraph.Node xmlNode;
      String viewType = graphNode.getViewType();
      if(graphNode instanceof GraphControl.ClusterFacade) {
        xmlNode = xmlCluster.addCluster();
        clusters.put(graphNode, xmlNode);
        xmlNode.setViewType(viewType);
      } else {
        xmlNode = xmlCluster.addNode();
        idLookup.put(graphNode, xmlNode.getID());
        xmlNode.setViewType(viewType);
      }
      setXMLNodeData(graphNode,xmlNode);
    }
    for(Enumeration e = clusters.keys(); e.hasMoreElements();) {
      GraphControl.ClusterFacade graphChildCluster
        = (GraphControl.ClusterFacade)e.nextElement();
      createXMLCluster(graphChildCluster, (XMLGraph.Cluster)clusters.get(graphChildCluster));
    }
    GraphControl.EdgeFacade[] edges = graphCluster.getEdges();
    for(int i=0; i<edges.length; i++) {
      String viewType = edges[i].getViewType();
      XMLGraph.Edge xmlEdge = xmlCluster.addEdge(
          (String)idLookup.get(edges[i].getStartNode()),
          (String)idLookup.get(edges[i].getEndNode()));
      xmlEdge.setViewType(viewType);
    }
  }
}
