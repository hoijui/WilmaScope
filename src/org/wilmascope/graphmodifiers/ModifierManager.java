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
package org.wilmascope.graphmodifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.wilmascope.control.WilmaMain;
import org.wilmascope.global.GlobalConstants;
import org.wilmascope.util.Registry;

/**
 * Provides a registry of graph generators. If you add new generators
 * (subclassing GraphModifier) then add a call to addGenerator for your class
 * in the constructor
 * 
 * @author dwyer
 *  
 */
public class ModifierManager extends Registry<GraphModifier>{

  private static ModifierManager instance = new ModifierManager();

  /**
   * Builds up the table of available generators and sets the default based on
   * the "DefaultGenerator" field in the WILMA_CONSTANTS.properties file.
   * 
   * @throws UnknownTypeException
   *           if the default modifier
   */
  private ModifierManager() {
    super("DefaultModifier","ModifierPlugins");
  }


  /**
   * There should only ever be one instance of this class. Access via the
   * following method.
   * 
   * @return the singleton instance of Registry
   */
  public static ModifierManager getInstance() {
    return instance;
  }
}
