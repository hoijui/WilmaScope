package org.wilmascope.control;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import org.wilmascope.columnlayout.ColumnLayout;
import org.wilmascope.columnlayout.NodeColumnLayout;
import org.wilmascope.fastlayout.FastLayout;
import org.wilmascope.fastlayout.InitFrame;
import org.wilmascope.fastlayout.ParamsPanel;
import org.wilmascope.viewplugin.TubeNodeView;
import org.wilmascope.forcelayout.BalancedEventClient;
import org.wilmascope.graph.Cluster;
import org.wilmascope.graph.LayoutEngine;
import javax.vecmath.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
//import com.borland.jbcl.layout.*;
import javax.swing.border.*;

public class TestGraph {

    String nodeView;
    String edgeView;

    public TestGraph(final GraphControl gc) {
      randomGraph(gc);
    }

    public void columnClusters(final GraphControl gc) {
        GraphControl.ClusterFacade r = gc.getRootCluster();
        r = r.addCluster();
        r.setLayoutEngine(new org.wilmascope.dotlayout.DotLayout(r.getCluster()));
        r.hide();
        ColumnCluster.setColumnStyle(ColumnCluster.DOTCOLUMNS);
        ColumnCluster ca = new ColumnCluster("ABC",r,1.0f,1.0f,0);
        ColumnCluster cb = new ColumnCluster("DEF",r,1.5f,1.5f,0);
        ColumnCluster cc = new ColumnCluster("GHI",r,1.0f,1.0f,0);

        GraphControl.NodeFacade a1 = ca.addNode(1.5f);
        GraphControl.NodeFacade a2 = ca.addNode(1.0f);
        GraphControl.NodeFacade a3 = ca.addNode(1.0f);
        GraphControl.NodeFacade a4 = ca.addNode(0.8f);
        GraphControl.NodeFacade b1 = cb.addNode(1f);
        GraphControl.NodeFacade b2 = cb.addNode(1.5f);
        GraphControl.NodeFacade b3 = cb.addNode(1.0f);
        GraphControl.NodeFacade b4 = cb.addNode(1.2f);
        GraphControl.NodeFacade c1 = cc.addNode(1.0f);
        GraphControl.NodeFacade c2 = cc.addNode(1.2f);
        GraphControl.NodeFacade c3 = cc.addNode(1.4f);
        GraphControl.NodeFacade c4 = cc.addNode(1.5f);
        r.addEdge(b1,a1,"Spline",0.01f).setColour(1f/4f,1f/4f,1f);
        r.addEdge(a2,c2,"Spline",0.008f).setColour(2f/4f,2f/4f,1f);
        r.addEdge(b3,c3,"Spline",0.01f).setColour(3f/4f,3f/4f,1f);
        r.addEdge(a4,b4,"Spline",0.015f).setColour(1f,1f,1f);
//        r.setBalancedEventClient(new BalancedEventClient() {
//            public void callback() {
//               System.out.println("BALANCED!!!!!");
//            }
//        });

        gc.unfreeze();
    }


    GraphControl.ClusterFacade root;
    GraphControl gc;

   // James' fast layout test
    public void randomGraph(GraphControl gc) {
        System.out.println("starting");
        this.gc = gc;
        this.root = root;
        root = gc.getRootCluster();

        InitFrame setup = new InitFrame("Fast-Layout Setup", this);
        setup.show();
    }

  public void setView(String nodeView, String edgeView) {
    this.nodeView = nodeView;
    this.edgeView = edgeView;
  }

  public boolean lineView() {
    return (nodeView.equals("LineNode"));
  }

  public void genRandom(int nodes, int edges, boolean threeD) {

    Cluster cluster = root.getCluster();
    //LayoutEngine layout = new FastLayout(cluster, threeD);
    //root.setLayoutEngine(layout);

    root.deleteAll();

// could make this loaded by fastlayout itself
    //if(params == null) params = new ParamsFrame((FastLayout)layout, gc, this, "Fast-Layout Parameters");
    //else params.setLayout((FastLayout)layout, this);
    //params.show();

    Vector nodevec = new Vector();
    GraphControl.NodeFacade temp;

    for (int i = 0; i < nodes; i++) {
      temp = root.addNode(nodeView);
      temp.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
      temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*5f, (rand.nextFloat()-0.5f)*5f, threeD ? (rand.nextFloat()-0.5f)*5f : 0f));
      //temp.setRadius(0.01f);
      nodevec.add(temp);
    }
    for (int i = 1; i < edges; i++) {
      GraphControl.NodeFacade a = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
      GraphControl.NodeFacade b = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
      if(a!=b) {
        root.addEdge(a,b, edgeView);
      } else {
        i--;
      }
    }
    for (int i = 0; i < nodes; i++) {
      temp = (GraphControl.NodeFacade)nodevec.get(i);
      org.wilmascope.graph.Node n = temp.getNode();
      if(n.getEdges().size()==0) {
        GraphControl.NodeFacade a = temp;
        do {
          a = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
        } while(a==temp);
        root.addEdge(a,temp, edgeView);
      }
    }
    for (int i = 0; i < nodevec.size(); i++) { // remove all disconnected nodes
      temp = (GraphControl.NodeFacade)nodevec.get(i);
      if(temp.getDegree() == 0) {
        root.removeNode(temp);    // have to check which one
        cluster.removeNode(temp.getNode()); //
        nodevec.remove(temp);
      }
    }
  }
  ////////////////////
  TreeMap lookup = new TreeMap();
  public void genRandom2(int isize, int jsize, boolean threeD) {

    Cluster cluster = root.getCluster();
    //LayoutEngine layout = new FastLayout(cluster, threeD);
    //root.setLayoutEngine(layout);

    root.deleteAll();

    for(int i=0;i<isize;i++) {
      for(int j=0;j<jsize;j++) {
        String lx = Integer.toString(i-1);
        String ly = Integer.toString(j-1);
        String x = Integer.toString(i);
        String y = Integer.toString(j);
        if(j>0) addEdge(x+","+ly,x+","+y, threeD);
        if(i>0) addEdge(lx+","+y,x+","+y, threeD);
      }
    }
  }
  private void addEdge(String start, String end, boolean threeD) {
    root.addEdge(lookupNode(start, threeD),lookupNode(end, threeD),edgeView);
  }
  private GraphControl.NodeFacade lookupNode(String id, boolean threeD) {
    GraphControl.NodeFacade n = (GraphControl.NodeFacade)lookup.get(id);
    if(n == null){
      n = root.addNode(nodeView);
      n.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
      n.setPosition(new Point3f((rand.nextFloat()-0.5f)*5f, (rand.nextFloat()-0.5f)*5f, threeD ? (rand.nextFloat()-0.5f)*5f : 0f));
      lookup.put(id,n);
    }
    return n;
  }


  public void genRandom3(int size, int number, boolean threeD) {

    Cluster cluster = root.getCluster();
    //LayoutEngine layout = new FastLayout(cluster, threeD);
    //root.setLayoutEngine(layout);

    root.deleteAll();

    Vector allnodes = new Vector();

    for (int j = 0; j < number; j++) { // create each cluster
      Vector nodevec = new Vector();
      GraphControl.NodeFacade temp;
      for (int i = 0; i < size; i++) {
        temp = root.addNode(nodeView);
        temp.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
        temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*5f, (rand.nextFloat()-0.5f)*5f, threeD ? (rand.nextFloat()-0.5f)*5f : 0f));
        //temp.setRadius(0.01f);
        nodevec.add(temp);
        allnodes.add(temp);
      }
      for (int i = 0; i < 2*size; i++) {
        GraphControl.NodeFacade a = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
        GraphControl.NodeFacade b = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
        if(a!=b) {root.addEdge(a,b, edgeView); } else {i--; }
      }
      for (int i = 0; i < nodevec.size(); i++) { // remove all disconnected nodes
        temp = (GraphControl.NodeFacade)nodevec.get(i);
        if(temp.getDegree() == 0) {
          root.removeNode(temp);    // have to check which one
          cluster.removeNode(temp.getNode()); //
          allnodes.remove(temp);
        }
      }
    }
    for (int i = 0; i < number*(size/20d); i++) {
        GraphControl.NodeFacade a = (GraphControl.NodeFacade)allnodes.get(rand.nextInt(allnodes.size()));
        GraphControl.NodeFacade b = (GraphControl.NodeFacade)allnodes.get(rand.nextInt(allnodes.size()));
        root.addEdge(a,b, edgeView);
    }

  }


    Random rand = new Random();
}

