package org.wilmascope.fastlayout;

/**
 * <p>Description: </p>
 * <p>$Id$ </p>
 * <p>@author </p>
 * <p>@version $Revision$</p>
 *  unascribed
 *
 */

public class Defaults {

  // how much to multiply the repulsion term by when calculating potential
  static final public int REPULSION = 2;

  // how much to multiply the attraction term by when calculating potential
  static final public int ATTRACTION = 5;

  // the size of a node footprint (in universe units) on the density matrix
  // the density will attenuate within this footprint
  static final public int NODE_FOOTPRINT = 5;

  // the maximum distance a node can jump during the boiling phase
  // this will decrease in subsequent phases
  // it is possible to let the node jump anywhere in the specified universe but that places
  // greated emphasis on the initial universe size and doesn't account for enforced smaller jumps
  // necessary during simmering and quenching - the paper is unclear on this
  // this factor would need to be tuned heavily for different sized graphs, it'd be nice
  // to try to calculate it automatically eventually
  static final public float BOIL_JUMP = 10f;

  // the indexes in the 'universe' matrix are integral yet the node positions are floats
  // this resolution defines how much to scale the floating point position
  // by before rounding to an int
  static final public int FIELD_RES = 2;

  // the number of times to run applyLayout before it is considered balanced
  // (fixed num as recommended by Eades)
  static final public int ITERATIONS = 200;

  // the fraction of the total iterations that are performed in the quenching phase
  // ( < 1 - BOIL_LENGTH)
  static final public double QUENCH_LENGTH = 0.5;

  // the fraction of the total iterations that are performed in the boiling phase
  // ( < 1 - QUENCH_LENGTH)
  static final public double BOIL_LENGTH = 0.2;

    // the frequency of barrier jumping during the boiling phase ( < 1)
  static final public double MAX_BARRIER_RATE = 0.25;

  // the frequency of barrier jumping at the end of the quenching phase ( < 1)
  static final public double MIN_BARRIER_RATE = 0.1;

  // the radius of the initial 'universe' containing the nodes
  // the universe will one day expand, via array-doubling, as necessary anyway
  static final public int FIELD_RADIUS = 500;

  // whether graph centring is enabled
  static final public boolean CENTRE_FLAG = false;

  // whether colour coding by potential is enabled
  static final public boolean COLOUR_FLAG = false;

  // whether extra eye-candy is enabled
  static final public boolean EYE_CANDY_FLAG = false;

  // the fraction of the max jump (during the boiling phase) that applies during the simmering phase
  static final public double SIMMER_RATE = 0.1;

}