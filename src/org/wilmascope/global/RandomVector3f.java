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
package org.wilmascope.global;

/*
 * Title:        WilmaToo
 * Description:  Sequel to the ever popular Wilma graph drawing engine
 * Copyright:    Copyright (c) 2001
 * Company:      WilmaOrg
 * @author Tim Dwyer
 * @version 1.0
 */
import java.util.Random;
import javax.vecmath.*;
/** Random vector generator
 */
public class RandomVector3f {
  private static Random random = new Random();

  // returns a random float in the range -0.5 to 0.5
  private static float getRandom() {
    return (random.nextFloat()-0.5f);
  }

  /**
   * @return A random Vector3f scaled by
   *  Constant: DefaultRandomVectorLength
   */
  public static Vector3f getVector3f() {
    //System.out.println("Warning: no length for RandomVector.getVector()");
    return getVector3f(constants.getFloatValue("DefaultRandomVectorLength"));
  }
  public static Point3f getPoint3f() {
    Point3f vec =  new Point3f(getRandom(), getRandom(), getRandom());
    return vec;
  }

  /**
   * @return a random unit vector scaled by length
   */
  public static Vector3f getVector3f(float length) {
    Vector3f vec =  new Vector3f(getRandom(), getRandom(), getRandom());
    vec.scale( length / vec.length() );
    return vec;
  }

  // The constants
  private static org.wilmascope.global.Constants constants = org.wilmascope.global.Constants.getInstance();
}
