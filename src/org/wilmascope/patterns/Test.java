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

package org.wilmascope.patterns;

/**
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular WilmaScope software
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaScope.org
 * @author Tim Dwyer
 * @version 1.0
 */

class Manager {
  private Manager() {
    System.out.println("Manager Created");
  }
  public static Manager getInstance() {
    return instance;
  }
  public void identify() {
    System.out.println("I'm the Manager! " + id);
  }
  public void setId(String id) {
    this.id = new String(id);
  }
  String id;
  private static final Manager instance = new Manager();
}
public class Test {
  public static void main(String args[]) {
    Manager m = Manager.getInstance();
    m.setId("Timbo!");
    Manager n = Manager.getInstance();
    n.identify();
  }
}
