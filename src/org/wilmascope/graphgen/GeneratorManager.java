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
package org.wilmascope.graphgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.wilmascope.control.WilmaMain;
import org.wilmascope.view.GraphElementView;
import org.wilmascope.view.ViewConstants;

/**
 * Provides a registry of graph generators. If you add new generators
 * (subclassing GraphGenerator) then add a call to addGenerator for your class
 * in the constructor
 * 
 * @author dwyer
 *  
 */
public class GeneratorManager {
  private static GeneratorManager instance = new GeneratorManager();

  /**
   * Builds up the table of available generators. If you create a new generator
   * then add a call to addGenerator for it here to make it available.
   */
  private GeneratorManager() {
    graphGenerators = new Hashtable<String, GraphGenerator>();
    load();
    defaultGenerator = graphGenerators.get("Random");
    // insert a call to addGenerator for your GraphGenerator here...
  }

  public void load() {
    try {
      String plugins = ViewConstants.getInstance().getProperty(
          "GeneratorPlugins");
      List classNames = new ArrayList();
      StringTokenizer st = new StringTokenizer(plugins, ",");
      while (st.hasMoreElements()) {
        String el = (String) st.nextElement();
        classNames.add(el);
      }
      String[] list = new String[classNames.size()];
      list = (String[]) classNames.toArray(list);
      for (int i = 0; i < list.length; i++) {
        System.out.println("Loading plugin file: " + list[i]);
        String name = list[i];
        Class c = Class.forName(name);
        GraphGenerator gen = (GraphGenerator) c.newInstance();
        addGenerator(gen);
      }
    } catch (Exception e) {
      WilmaMain.showErrorDialog("WARNING: Couldn't load plugins... check that you've listed the plugin classnames in the properties file",e);
    }
  }

  /**
   * There should only ever be one instance of this class. Access via the
   * following method.
   * 
   * @return the singleton instance of GeneratorManager
   */
  public static GeneratorManager getInstance() {
    return instance;
  }

  private void loadGraphGenerators() {
  }

  public String[] getTypeList() {
    return graphGenerators.keySet().toArray(new String[graphGenerators.size()]);
  }

  public GraphGenerator getGenerator(String type) {
    return graphGenerators.get(type);
  }

  public GraphGenerator getDefault() {
    return defaultGenerator;
  }

  public Collection<GraphGenerator> getGraphGenerators() {
    return graphGenerators.values();
  }

  public void addGenerator(GraphGenerator g) {
    graphGenerators.put(g.getName(), g);
  }

  Hashtable<String, GraphGenerator> graphGenerators;

  GraphGenerator defaultGenerator;
}
