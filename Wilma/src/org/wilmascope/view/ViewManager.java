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

package org.wilmascope.view;

import java.util.*;
import java.io.*;
import javax.swing.ImageIcon;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */
/**
 * This class manages a 'registry' of view object prototypes for
 * nodes and edges.  It implements the 'Singleton' pattern to ensure that
 * only one instance is available to the System, a reference to which may
 * be obtained using the {@link #getInstance()} method.
 */
public class ViewManager {
  /**
   * @return a reference to the Singleton instance of this class.
   */
  public static ViewManager getInstance() {
    return instance;
  }
  // private constructor so that the class cannot be extanciated externally
  private ViewManager() {
    nodeReg = new Registry();
    edgeReg = new Registry();
  }
  public class UnknownViewTypeException extends Exception {
    public UnknownViewTypeException(String viewType) {
      super("No known view type: "+viewType);
    }
  }
  public class Registry {
    public String[] getViewNames() {
      return (String[])views.keySet().toArray(new String[0]);
    }
    public GraphElementView create(String type)
    throws UnknownViewTypeException {
      GraphElementView view;
      if((view = (GraphElementView)views.get(type))==null) {
        throw new UnknownViewTypeException(type);
      }
      return (GraphElementView)view.clone();
    }
    public GraphElementView getDefaultViewPrototype()
    throws UnknownViewTypeException {
      if(defaultView == null) {
        throw new UnknownViewTypeException("Default view not set!");
      }
      return (GraphElementView)defaultView;
    }
    public GraphElementView create()
    throws UnknownViewTypeException {
      if(defaultView == null) {
        throw new UnknownViewTypeException("Default view not set!");
      }
      return (GraphElementView)defaultView.clone();
    }
    public void setDefaultView(String type)
    throws UnknownViewTypeException {
      if((defaultView = (GraphElementView)views.get(type))==null) {
        throw new UnknownViewTypeException(type);
      }
    }
    public ImageIcon getIcon(String type)
    throws UnknownViewTypeException {
      GraphElementView view;
      if((view = (GraphElementView)views.get(type))==null) {
        throw new UnknownViewTypeException(type);
      }
      return view.getIcon();
    }
    public ImageIcon getIcon()
    throws UnknownViewTypeException {
      if(defaultView == null) {
        throw new UnknownViewTypeException("Default view not set!");
      }
      return defaultView.getIcon();
    }
    public void addPrototypeView(GraphElementView prototype) {
      views.put(prototype.getTypeName(), prototype);
    }
    private Hashtable views = new Hashtable();
    private GraphElementView defaultView;
  }
  public void loadViews(File directory)
  throws IOException {
    loadViews(directory,null);
  }
  public void loadViews(File directory, String packageName)
  throws IOException {
    String list[]=directory.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith("View.class");
      }
    });
    if(list==null) {
      throw new IOException("\""+directory.getName()+"\" directory not found.");
    }
    for(int i=0; i<list.length; i++) {
      System.out.println("Loading plugin file: " + list[i]);
      String name = list[i].substring(0,list[i].length()-6);
      try {
        Class c;
        if(packageName!=null && packageName.length()!=0) {
          c = Class.forName(packageName + "." + name);
        } else {
          c = Class.forName(name);
        }
        GraphElementView view = (GraphElementView)c.newInstance();
        addPrototypeView(view);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
  public void addPrototypeView(GraphElementView view) {
    if(view instanceof NodeView) {
      nodeReg.addPrototypeView(view);
    } else if(view instanceof EdgeView) {
      edgeReg.addPrototypeView(view);
    }
  }
  public Registry getNodeViewRegistry() {
    return nodeReg;
  }
  public Registry getEdgeViewRegistry() {
    return edgeReg;
  }
  public NodeView createNodeView(String type)
  throws UnknownViewTypeException {
    return (NodeView)nodeReg.create(type);
  }
  public EdgeView createEdgeView(String type)
  throws UnknownViewTypeException {
    return (EdgeView)edgeReg.create(type);
  }
  public NodeView createNodeView()
  throws UnknownViewTypeException {
    return (NodeView)nodeReg.create();
  }
  public EdgeView createEdgeView()
  throws UnknownViewTypeException {
    return (EdgeView)edgeReg.create();
  }
  public String getDefaultEdgeViewType() {
    try {
      return edgeReg.getDefaultViewPrototype().getTypeName();
    } catch(UnknownViewTypeException e) {
      System.out.println(e);
      return null;
    }
  }
  public String getDefaultNodeViewType() {
    try {
      return nodeReg.getDefaultViewPrototype().getTypeName();
    } catch(UnknownViewTypeException e) {
      System.out.println(e);
      return null;
    }
  }
  private static final ViewManager instance = new ViewManager();
  private Registry nodeReg;
  private Registry edgeReg;
}
