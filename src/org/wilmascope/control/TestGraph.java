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

  //  public TestGraph(GraphControl gc) {
  // James' fast layout test
    public void randomGraph(GraphControl gc) {
        System.out.println("starting");
        this.gc = gc;
        this.root = root;
        root = gc.getRootCluster();
//        root = root.addCluster();
        Cluster cluster = root.getCluster();
        LayoutEngine layout = new FastLayout(cluster);
        root.setLayoutEngine(layout);
//        ((FastLayout)root.getLayoutEngine()).ITERATIONS = 500;
//        root.hide();
//        System.out.println("adding nodes...");


        InitFrame setup = new InitFrame(root,"Fast-Layout Setup", this);
        setup.show();


/*        Random rand = new Random();
        Vector nodevec = new Vector();
        GraphControl.NodeFacade temp;
        for (int i = 0; i < 10; i++) {
          temp = root.addNode();
          temp.setColour(1f,0f,0f);
          temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
          nodevec.add(temp);
        }
        for (int i = 1; i < nodevec.size(); i++) {
          root.addEdge((GraphControl.NodeFacade)nodevec.get(1),(GraphControl.NodeFacade)nodevec.get(i),"Plain Edge");
        }

// code reuse would be nice but who cares
        nodevec = new Vector();
        for (int i = 0; i < 10; i++) {
          temp = root.addNode();
          temp.setColour(0f,0f,1f);
          temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
          nodevec.add(temp);
        }
        for (int i = 1; i < nodevec.size(); i++) {
          root.addEdge((GraphControl.NodeFacade)nodevec.get(1),(GraphControl.NodeFacade)nodevec.get(i),"Plain Edge");
        }

        nodevec = new Vector();
        for (int i = 0; i < 10; i++) {
          temp = root.addNode();
          temp.setColour(0f,1f,0f);
          temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
          nodevec.add(temp);
        }
        for (int i = 1; i < nodevec.size(); i++) {
          root.addEdge((GraphControl.NodeFacade)nodevec.get(1),(GraphControl.NodeFacade)nodevec.get(i),"Plain Edge");
        }
*/


/*
        Random rand = new Random();
        Vector nodevec = new Vector();
        GraphControl.NodeFacade temp;
        try {

          BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
          System.out.println("how many nodes? ");
          int nodes = Integer.parseInt(in.readLine());
          System.out.println("how many edges? ");
          int edges = Integer.parseInt(in.readLine());
          in.close();

          for (int i = 0; i < nodes; i++) {
            temp = root.addNode();
            temp.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
            temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
            nodevec.add(temp);
          }
          for (int i = 1; i < edges; i++) {
            root.addEdge((GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size())),(GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size())),"Plain Edge");
          }

        }
        catch(Exception e) {
          System.exit(1);
        }
*/
/*
        GraphControl.NodeFacade a = root.addNode();
        GraphControl.NodeFacade b = root.addNode();
        a.setColour(1f,0f,0f);
        b.setColour(0f,1f,0f);
        a.setPosition(new Point3f(1f,0f,0f));
        b.setPosition(new Point3f(0f,0f,0f));
        root.addEdge(a,b,"Plain Edge");
*/
/*
        GraphControl.NodeFacade a = root.addNode();
        GraphControl.NodeFacade b = root.addNode();
        GraphControl.NodeFacade c = root.addNode();
        GraphControl.NodeFacade d = root.addNode();
        GraphControl.NodeFacade e = root.addNode();
        GraphControl.NodeFacade f = root.addNode();
        a.setColour(1f,0f,0f);
        b.setColour(0f,1f,0f);
        c.setColour(0f,0f,1f);
        d.setColour(1f,0f,1f);
        e.setColour(0f,1f,1f);
        f.setColour(1f,1f,0f);
        a.setPosition(new Point3f(1f,0f,0f));
        b.setPosition(new Point3f(0f,1f,0f));
        c.setPosition(new Point3f(0f,0f,0f));
        d.setPosition(new Point3f(1f,1f,0f));
        e.setPosition(new Point3f(0f,0f,0f));
        f.setPosition(new Point3f(1f,0.5f,0f));
        System.out.println("adding edges...");
        root.addEdge(a,b,"Plain Edge");
        root.addEdge(a,c,"Plain Edge");
        root.addEdge(c,d,"Plain Edge");
        root.addEdge(a,d,"Plain Edge");
        root.addEdge(e,f,"Plain Edge");
        System.out.println("initialisation fine");
        System.out.println(a.getDegree());
*/
/*
        GraphControl.NodeFacade a = root.addNode();
        GraphControl.NodeFacade b = root.addNode();
        GraphControl.NodeFacade c = root.addNode();
        GraphControl.NodeFacade d = root.addNode();
        GraphControl.NodeFacade e = root.addNode();
        GraphControl.NodeFacade f = root.addNode();
        GraphControl.NodeFacade g = root.addNode();
        GraphControl.NodeFacade h = root.addNode();
        GraphControl.NodeFacade i = root.addNode();
        GraphControl.NodeFacade j = root.addNode();
        a.setColour(1f,1f,0f);
        b.setColour(1f,1f,0f);
        c.setColour(1f,0f,0f);
        d.setColour(1f,0f,0f);
        e.setColour(1f,1f,0f);
        f.setColour(0f,0f,1f);
        g.setColour(0f,0f,1f);
        h.setColour(0f,0f,1f);
        i.setColour(0f,0f,1f);
        j.setColour(0f,0f,1f);
        a.setPosition(new Point3f(1f,0f,0f));
        b.setPosition(new Point3f(0f,1f,0f));
        c.setPosition(new Point3f(0f,0f,0f));
        d.setPosition(new Point3f(1f,1f,0f));
        e.setPosition(new Point3f(0f,0f,0f));
        f.setPosition(new Point3f(1f,0.5f,0f));
        g.setPosition(new Point3f(0.5f,0f,0f));
        h.setPosition(new Point3f(0f,0.5f,0f));
        i.setPosition(new Point3f(0f,0f,0f));
        j.setPosition(new Point3f(0.5f,0.5f,0f));
        root.addEdge(a,c,"Plain Edge");
        root.addEdge(a,d,"Plain Edge");
        root.addEdge(b,c,"Plain Edge");
        root.addEdge(c,d,"Plain Edge");
        root.addEdge(d,e,"Plain Edge");
        root.addEdge(c,f,"Plain Edge");
        root.addEdge(d,f,"Plain Edge");
        root.addEdge(f,g,"Plain Edge");
        root.addEdge(f,i,"Plain Edge");
        root.addEdge(g,h,"Plain Edge");
        root.addEdge(i,j,"Plain Edge");
*/
/*
        GraphControl.NodeFacade a = root.addNode();
        GraphControl.NodeFacade b = root.addNode();
        GraphControl.NodeFacade c = root.addNode();
        GraphControl.NodeFacade d = root.addNode();
        GraphControl.NodeFacade e = root.addNode();
        a.setPosition(new Point3f(1.5f,0.7f,0f));
        b.setPosition(new Point3f(0f,0f,0f));
        c.setPosition(new Point3f(1f,0f,0f));
        d.setPosition(new Point3f(1f,1f,0f));
        e.setPosition(new Point3f(0f,1f,0f));
        root.addEdge(a,b,"Plain Edge");
        root.addEdge(a,c,"Plain Edge");
        root.addEdge(a,d,"Plain Edge");
        root.addEdge(a,e,"Plain Edge");
        a.setPosition(((FastLayout)layout).computeCentroid(a.getNode()));
*/
//        gc.unfreeze();
    }

  public void genRandom(int nodes, int edges) {
    Vector nodevec = new Vector();
    GraphControl.NodeFacade temp;

    for (int i = 0; i < nodes; i++) {
      temp = root.addNode();
      temp.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
      temp.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
      temp.setRadius(0.01f);
      nodevec.add(temp);
    }
    for (int i = 1; i < edges; i++) {
      GraphControl.NodeFacade a = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
      GraphControl.NodeFacade b = (GraphControl.NodeFacade)nodevec.get(rand.nextInt(nodevec.size()));
      root.addEdge(a,b);
    }
    gc.unfreeze();
  }
  ////////////////////
  TreeMap lookup = new TreeMap();
  public void genRandom2(int isize, int jsize) {
    for(int i=0;i<isize;i++) {
      for(int j=0;j<jsize;j++) {
        String lx = Integer.toString(i-1);
        String ly = Integer.toString(j-1);
        String x = Integer.toString(i);
        String y = Integer.toString(j);
        if(j>0) addEdge(x+","+ly,x+","+y);
        if(i>0) addEdge(lx+","+y,x+","+y);
      }
    }
    gc.unfreeze();
  }
  private void addEdge(String start, String end) {
    root.addEdge(lookupNode(start),lookupNode(end),"LineEdge");
  }
  private GraphControl.NodeFacade lookupNode(String id) {
    GraphControl.NodeFacade n = (GraphControl.NodeFacade)lookup.get(id);
    if(n == null){
      n = root.addNode("LineNode");
      n.setColour(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
      n.setPosition(new Point3f((rand.nextFloat()-0.5f)*10, (rand.nextFloat()-0.5f)*10, 0f));
      lookup.put(id,n);
    }
    return n;
  }

    Random rand = new Random();
}

