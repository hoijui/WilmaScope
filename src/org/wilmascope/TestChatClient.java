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

/** The following is a simple demo of a Wilma CORBA client
 *
 * Author: Tim Dwyer
 *
 * Creates two nodes, one a bit larger than the other, and an edge between them
 * Should really do something more exciting but it should make you realise
 * wilma's potential on give you examples of everything you need to build
 * some really funky tools.
 *
 * Also adds example callbacks for when the smaller node is "nudged" and
 * when the graph reaches a settled (balanced) state.  You can probably
 * imagine that you could do something a lot more exciting with these, ie
 * front end to a browsing tool?
 */
package org.wilmascope;

import org.wilmascope.chat.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import java.io.*;
import java.util.*;

/** A class for handling callbacks from a node when it is nudged
 */
class PickingClientServant extends _PickingClientImplBase
{
    public void callback(int x, int y, short button)
    {
        System.out.println("Node clicked!!!, at x="+x+",y="+y+",button="+button);
    }
}

/** A class for handling callbacks from when the graph settles
 */
class BalancedEventClientServant extends _BalancedEventClientImplBase
{
  public void callback()
  {
    System.out.println("GraphBalanced!!");
  }
}

public class TestChatClient
{
  static Vector argList;
  static char t[] = new char[1000];
  public static void main(String args[])
  {
    try{
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // get the root naming context
      org.omg.CORBA.Object objRef =
        orb.resolve_initial_references("NameService");
      NamingContext ncRef = NamingContextHelper.narrow(objRef);

      // resolve the Object Reference in Naming
      NameComponent nc = new NameComponent("WilmaGraph", "Object");
      NameComponent path[] = {nc};

      // Obtain a reference to the server's GraphControl object
      GraphControl gc
        = GraphControlHelper.narrow(ncRef.resolve(path));

      // Obtain a reference to the root cluster of the graph
      Cluster r = gc.getRootCluster();

      // Create a callback (or listener object) which provides a method
      // which the server will call when the graph is balanced (no longer
      // moving)
      BalancedEventClientServant balancedEventClientRef = new BalancedEventClientServant();
      orb.connect(balancedEventClientRef);
      r.setBalancedEventClient(balancedEventClientRef);

      // Create a node
      Node a = addNode(r);

      // Create a callback which will get called when the smaller node
      // gets nudged... ie, click the "Nudge Node" button, then click on
      // the smaller node
      PickingClientServant pickingClientRef = new PickingClientServant();
      orb.connect(pickingClientRef);
      a.setPickingClient(pickingClientRef);
      a.setLabel("Nudge Me!");

      // Create another node
      Node b = addNode(r);
      // b.setRadius(0.2f); set radius isn't too healthy just at the moment.
      // if anyone wants it I'll fix it.  Otherwise, stuff it.

      // This is a nice lilac colour on my monitor.  It'll probably look like
      // spew elsewhere but you get the idea
      b.setColour(new Colour(0.7f,0.4f,0.9f));

      // Create an edge between the two nodes... if we were to try to create
      // an edge between a node and itself or something similarly silly an
      // exception would be thrown
      addEdge(r,a,b);

      // wait for any callbacks
      java.lang.Object sync = new java.lang.Object();
      synchronized (sync) {
        sync.wait();
      }

    } catch (Exception ex) {
      System.out.println("ERROR : " + ex) ;
      ex.printStackTrace(System.out);
    }
  }
  static private Node addNode(Cluster c) {
    Node n = c.addNewNode();
    c.unfreeze();
    return n;
  }
  static private Edge addEdge(Cluster c, Node a, Node b) {
    Edge e = c.addNewEdge(a,b);
    c.unfreeze();
    return e;
  }
}
