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
package org.wilmascope.layoutregistry;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import org.wilmascope.graph.LayoutEngine;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

/**
 * This class provides a manager or registry of all the available layout 
 * engines.  This class implements
 * the Singleton design pattern (Gamma et al.) such that there can only ever
 * be one instance in the system and a reference to that instance can be
 * obtained by calling the static {link #getInstance()} method from anywhere.
 */
public class LayoutManager {
  public static LayoutManager getInstance() {
    return instance;
  }
  private LayoutManager() {
  }
  public class UnknownLayoutTypeException extends Exception {
    public UnknownLayoutTypeException(String layoutType) {
      super("No known layout type: " + layoutType);
    }
  }
  public LayoutEngine createLayout(String layoutType)
    throws UnknownLayoutTypeException {
    LayoutEngine prototype = (LayoutEngine) layoutEngines.get(layoutType);
    if (prototype == null) {
      throw (new UnknownLayoutTypeException(layoutType));
    }
    return (LayoutEngine)prototype.create();
  }
  public void addPrototypeLayout(LayoutEngine prototype) {
    layoutEngines.put(prototype.getName(), prototype);
  }
  public Collection getAvailableLayoutEngines() {
    return layoutEngines.values();
  }
  private Hashtable layoutEngines = new Hashtable();
  private static final LayoutManager instance = new LayoutManager();
  public LayoutEngine[] getAll() {
  	LayoutEngine[] all = new LayoutEngine[layoutEngines.size()];
  	int i=0;
  	for(Enumeration e = layoutEngines.elements(); e.hasMoreElements();) {
  		all[i++]=(LayoutEngine)e.nextElement();
  	}
  	return all;
  }
  /**
   * @return an array of type names
   */
  public String[] getTypeList() {
  	String[] typeList = new String[layoutEngines.size()];
  	int i=0;
  	for(Enumeration e=layoutEngines.keys();e.hasMoreElements();) {
  		typeList[i++]=(String)e.nextElement();
  	}
    return typeList;
  }
}
