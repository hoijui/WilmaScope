package org.wilmascope.gmlparser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import org.wilmascope.control.GraphControl;
/**
 * @author dwyer
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileImporter {
  static GMLParser parser;
  public static void load(GraphControl g, String filename) {
    try {
      final GraphControl.ClusterFacade r = g.getRootCluster();
      System.out.println(filename);
      FileInputStream f = new FileInputStream(filename);
      if(parser == null) {
        parser = new GMLParser(f);
      } else {
        GMLParser.ReInit(f);
      }
      GMLParser.graph(new GraphClient() { 
        Hashtable nodes = new Hashtable(); 
        public void addNode(String id, String label) {
          GraphControl.NodeFacade n = r.addNode();
          n.setLabel(label);
          nodes.put(id,n);
        }
        public void addEdge(
          String startID,
          String endID,
          String label,
          String arrowPosition) {
            GraphControl.NodeFacade n = r.addNode("Box");
            GraphControl.NodeFacade start = (GraphControl.NodeFacade)nodes.get(startID);
            GraphControl.NodeFacade end = (GraphControl.NodeFacade)nodes.get(endID);
            n.setLabel(label);
            if(arrowPosition.equals("both")) {
              r.addEdge(n, start,"Arrow");
              r.addEdge(n, end,"Arrow");
            } else if(arrowPosition.equals("last")) {
              r.addEdge(start,n);
              r.addEdge(n,end,"Arrow");
            }
        }
        public void addEdge(
          String startID,
          String endID,
          String arrowPosition) {
            GraphControl.NodeFacade start = (GraphControl.NodeFacade)nodes.get(startID);
            GraphControl.NodeFacade end = (GraphControl.NodeFacade)nodes.get(endID);
            if(arrowPosition.equals("both")) {
              r.addEdge(start, end, "Arrow");
              r.addEdge(end, start,"Arrow");
            } else if(arrowPosition.equals("last")) {
              r.addEdge(start,end,"Arrow");
            } else {
              r.addEdge(start,end);
            }
        }

      });
      r.unfreeze();
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(ParseException e) {
      e.printStackTrace();
    }
  }
}
