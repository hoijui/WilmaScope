package org.wilmascope.fastlayout;

import org.wilmascope.graph.*;
import org.wilmascope.control.GraphControl.*;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Color3f;
import java.util.Vector;
import java.util.Random;
import java.awt.Color;

/**
 * <p>Title:        FastLayout</p>
 * <p>Description:  Wilma layout engine implementing a linear time fast force directed placement algorithm </p>
 * <p>Copyright:    Copyright (c) 2001</p>
 * @author James Cowling
 * @company WilmaScope.org
 * @version 0.9
 */

 // move a lot of this stuff to the fastnodelayout and fastedgelayout et al classes

 // the z coordinate of each position is ignored - only 2D atm!

 // either move the root to the origin or use the coordinates as relative to the root

 // implement mass of nodes & natural length of edges

 // possible source of error when repeatedly adding and subtracting from density matrix

 // replace all the for loops with the node.resetIterator type

public class FastLayout implements LayoutEngine{

  Cluster root;

  Random rand = new Random();

  NodeList nodes;

  // lots of constants that are unfortunately rather important
  // some haven't been implemented yet:

  // all these constants should be put in a package-public static constants class

  // they have been set to public non-const for testing purposes

  // make sure i don't call resetIterator() inside a nested loop (and thus resetting it for the outside as well)

  //
  // in the process of moving these to Defaults, making them variable via UI etc
  //

  int repulsion = Defaults.REPULSION;
  int attraction = Defaults.ATTRACTION;
  int nodeFootprint = Defaults.NODE_FOOTPRINT;

  // indicates the min level the density should attenuate to at the edge of the
  // node footprint
  /*static final*/ public double MIN_DENSITY = 0.04;

  float boilJump = Defaults.BOIL_JUMP;

  int fieldRadius = Defaults.FIELD_RADIUS;
  int fieldRes = Defaults.FIELD_RES;
  int totalIterations = Defaults.ITERATIONS;

  // the fraction of the boiling-phase max jump (boilJump) that applies in the simmering phase ( < 1)
  double simmerRate = Defaults.SIMMER_RATE;

  double maxBarrierRate = Defaults.MAX_BARRIER_RATE;
  double minBarrierRate = Defaults.MIN_BARRIER_RATE;
  double boilLength = Defaults.BOIL_LENGTH;
  double quenchLength = Defaults.QUENCH_LENGTH;

  // the amount to decrement maxJump by each iteration during quenching to linearly
  // decrease it from boilJump to simmerRate
  double JUMP_DEC;

  // the amount to decrement barrierRate by each iteration during quenching to linearly
  // decrease it from maxBarrierRate to minBarrierRate
  double BARRIER_DEC;

  // enumeration of phase names for convenience sake
  static final int BOILING = 0;
  static final int QUENCHING = 1;
  static final int SIMMERING = 2;

  // the current phase
  int phase = BOILING;

  // the matrix storing the densities
  DensityMatrix universe;

  // the number of times applyLayout has been run
  int iterations = 0;

  // the current maximum jump length
  float maxJump = boilJump;

  // the current barrier jump rate
  double barrierRate = maxBarrierRate;

  // if graph centring should be enabled on each iteration
  boolean centreFlag = Defaults.CENTRE_FLAG;

  // if colour coding by potential is enabled
  boolean colourFlag = Defaults.COLOUR_FLAG;

  // if extra eye-candy is enabled
  boolean eyeCandyFlag = Defaults.EYE_CANDY_FLAG;

  ParamsFrame params;

  Vector colours = new Vector();

  public FastLayout(Cluster root) {
    this.root = root;
    root.setLayoutEngine(this);
    nodes = root.getNodes();
    updateJumpDec();
    updateBarrierDec();
    // won't have to pass these when i separate constants
    universe = new DensityMatrix(2*fieldRadius, 2*fieldRadius, fieldRes, nodeFootprint, MIN_DENSITY);
//    System.err.println(JUMP_DEC);
    params = new ParamsFrame(this, "Fast-Layout Parameters");
    params.show();
    calcColours();

  }


  // must be run first to set initial densities
  public void calculateLayout() {

    // extremely wasteful and inefficient and will probably ruin time bounds
    // only here if calculateLayout keeps getting called
    // when i sort out the interaction between wilma and FastLayout i can get rid
    // of this and enforce a call to update() or something when a node is added
    universe.setZero();
    universe.set(nodes);
  }


  public boolean applyLayout() {

    iterations++;
//    System.err.println(iterations);

// replace some of this with an updatePhase() method, and use that in setBoilLen etc
    switch(phase) {
      case BOILING:
        if (iterations > boilLength*totalIterations) { // should cache these multiplications
//          System.err.println("entering quenching phase...");
//          System.err.println("iterations = " + iterations);
          phase = QUENCHING;
        }
        break;
      case QUENCHING:

        if (iterations > (quenchLength+boilLength)*totalIterations) {
//          System.err.println("entering simmering phase...");
//          System.err.println("iterations = " + iterations);
          phase = SIMMERING;
          barrierRate = 0;
        }
        else {
          maxJump -= JUMP_DEC;
          barrierRate -= BARRIER_DEC;
        }
        break;
    }

//    System.err.println(maxJump);

    for(nodes.resetIterator(); nodes.hasNext();) {
      Node current = nodes.nextNode();
      // should be a way of sensing an increase in potential without having
      // to actually move the node
      double oldPotential = getPotential(current, false);
      Point3f oldPosition = new Point3f(current.getPosition());
      double newPotential = 0d;

      boolean jump = (rand.nextDouble() < barrierRate); // true the fraction of times specified by barrierRate


// get rid of this 'if' section if i want to revert to pre-centroid jumping
      if(jump) {
        current.setPosition(computeCentroid(current));
        universe.update(oldPosition, current.getPosition(), current);

        // only calculating this because of colour coding
        if (colourFlag) newPotential = getPotential(current, false);
      }
      else {
        current.reposition(getRandomOffset());                        // to be combined into method in
        universe.update(oldPosition, current.getPosition(), current); // fastnodelayout

        newPotential = getPotential(current, false);
// get rid of jump boolean here, and in getPotential, once I'm satisfied that weighted centroid is working
        if(newPotential > oldPotential) {
          universe.update(current.getPosition(), oldPosition, current); // to be combined into method in
          current.setPosition(oldPosition);                             // fastnodelayout
          if (colourFlag) newPotential = oldPotential;
        }
      }

      // scale the current colour by newPotential
      if (colourFlag) {
//        System.err.println("colour coding with index: " + newPotential);
        if(newPotential < 100) ((NodeFacade)(current.getUserFacade())).setColour((Color)colours.get((int)newPotential));
        else ((NodeFacade)(current.getUserFacade())).setColour(Color.red);
      }

      // extremely expensive and ridiculous... but kinda fun :)
      if (eyeCandyFlag) {
        EdgeList edges = current.getEdges();
        for(edges.resetIterator(); edges.hasNext();) {
          Edge temp = edges.nextEdge();
          Color colour1 = ((NodeFacade)temp.getStart().getUserFacade()).getColour();
          Color colour2 = ((NodeFacade)temp.getEnd().getUserFacade()).getColour();
          Color3f edgeColour = new Color3f();
          edgeColour.interpolate(new Color3f(colour1), new Color3f(colour2), 0.5f);
          ((EdgeFacade)temp.getUserFacade()).setColour(edgeColour.get());
        }
        current.setRadius((float)(newPotential/400d));
      }

    }


//    System.err.println(systemEnergy());

// have a flag turning this on and off
    if(centreFlag) recentreGraph();

    return (iterations > totalIterations); // decide on a method to determine stability
  }


  // will returrn the total energy of the system
  // once this is calculated initially it can be automatically updated in
  // applyLayout() each time a potential changes
  // for the moment it just manually calculates the total potential
  public double systemEnergy() {
    double total = 0;
    for(nodes.resetIterator(); nodes.hasNext();) {
      total += getPotential(nodes.nextNode(), false);
    }
    return total;
  }


  private void recentreGraph() {
    Point3f centre = nodes.getBarycenter();
    // subtract current centre vector from all nodes to bring their centre back to the origin
    for(nodes.resetIterator(); nodes.hasNext();) {
      nodes.nextNode().getPosition().sub(centre);
    }
    // reset density matrix - will be expensive
    universe.setZero();
    universe.set(nodes);
  }


  // will have to take into account a lot of factors, including the phase:
  // boiling, quenching, simmering etc
  // return a position offset within the maxJump factor
  private Vector3f getRandomOffset() {
    float x = (rand.nextFloat()-0.5f)*2*maxJump;
    float y = (rand.nextFloat()-0.5f)*2*maxJump;
    return new Vector3f(x,y,0f);
  }

  // might make more sense in the node class or node layout
  // need to do a lot of stuff to calculate the potential
  // if jump is true, barrier jumping is enabled for the node and
  // it will ignore the repulsion (density) term to avoid local minima
  //
  // the scale factors could be calculated outside the loop etc to increase
  // efficiency if it really comes down to that - there will be many ways to
  // tune the algorithm
  private double getPotential(Node node, boolean jump) {
    // any fancy way of taking the sum?
    double potential = 0;
    // might be faster to cache the degree
    for(int i = 0; i < node.getDegree(); i++) {
      Edge edge = node.getEdges().get(i);
      edge.recalculate(); // don't know why i have to call this... but i do
      potential += attraction*edge.getWeight()*edge.getLength()*edge.getLength()*edge.getStart().getMass()*edge.getEnd().getMass();
      // this is modified from that given in the research paper - it creates greater attraction for heavier nodes
    }
    if(!jump) potential += repulsion*universe.get(node.getPosition())*node.getMass(); // modified again here
//    System.err.println("Density: " + universe.get(node.getPosition()));
    return potential;
  }


  // for barrier jumping
  // computes a weighted centroid over the adjacent nodes and returns the position
  //
  // Weighted centroid = Sum(edgeWeight*position)/Sum(weight)
  //
  public Point3f computeCentroid(Node node) {
    if(node.getDegree() == 0) return node.getPosition();
    Point3f centroid = new Point3f();
    float totalWeight = 0f;

    EdgeList edges = node.getEdges();
    for(edges.resetIterator(); edges.hasNext();) {
      Edge current = edges.nextEdge();
      float weight = current.getWeight();
      Point3f scaledPos = new Point3f(current.getNeighbour(node).getPosition());
      scaledPos.scale(weight);
      centroid.add(scaledPos);
      totalWeight += weight;
    }
    centroid.scale(1f/totalWeight);// average over weights
    return centroid;
  }

  // flattens the graph ie. sets all z values to zero
  public void flattenGraph() {
    for(nodes.resetIterator(); nodes.hasNext();) {
      nodes.nextNode().getPosition().z = 0f;
    }
  }



  // establishes colours for green, amber & red, then interpolates all the in-between
  // colours to determine the colour spectrum (of length 100) for the nodes
  private void calcColours() {
    Color3f green = new Color3f(Color.green);
    Color3f orange = new Color3f(Color.orange);
    Color3f red = new Color3f(Color.red);

    colours.add(green.get());
    // interpolate the next 59 colours between green and oragne:
    for(int i = 0; i < 60; i++) {
      Color3f temp = new Color3f();
// calculate the displacement so i won't need to do a division each time
      float alpha = (float)i/(float)59;
      temp.interpolate(green, orange, alpha);
      colours.add(temp.get());
    }

    colours.add(orange.get());
    // interpolate the next 38 colours between orange and red:
    for(int i = 0; i < 39; i++) {
      Color3f temp = new Color3f();
// calculate the displacement so i won't need to do a division each time
      float alpha = (float)i/(float)38;
      temp.interpolate(orange, red, alpha);
      colours.add(temp.get());
    }

    colours.add(red.get());
  }





  private void updateJumpDec() {
    JUMP_DEC = (boilJump - boilJump*simmerRate) / (quenchLength*totalIterations);
  }

  private void updateBarrierDec() {
    BARRIER_DEC = (maxBarrierRate - minBarrierRate) / (quenchLength*totalIterations);
  }

  public void setIterations(int val) {
    totalIterations = val;
    updateJumpDec();
    updateBarrierDec();
  }
  public void setAttract(int val) {
    attraction = val;
  }

  public void setRepel(int val) {
    repulsion = val;
  }

  public void setFootprint(int val) {
    nodeFootprint = val; // the value should be set directly in DensityMatrix eventually
    universe.setFootprint(nodeFootprint);
  }

  public void setBoilJump(int val) {

    //scale current maxJump using:
    //
    //     maxJump - boilJump         boilJump
    // -------------------------- = -------------
    //  newMaxJump - newBoilJump     newBoilJump
    //

System.err.println("Old boilJump: " + boilJump);
System.err.println("Old maxJump: " + maxJump);
    maxJump = val*(maxJump-boilJump)/boilJump + val;
    boilJump = val;
System.err.println("New boilJump: " + boilJump);
System.err.println("New maxJump: " + maxJump);

    updateJumpDec();
  }

  public void setRes(int val) {
    fieldRes = val;
    universe.setRes(fieldRes);
  }

  public void setRadius(int val) {
    fieldRadius = val;
    universe.setRadius(fieldRadius);
    System.err.println("Reinitializing densities... crisis averted :)\n");
    universe.set(nodes);
  }

  public void setSimmerRate(double val) {
    simmerRate = val;
    if(phase == SIMMERING) maxJump = (float)(boilJump*simmerRate);
    else updateJumpDec();
  }

  public void setBoilLen(double val) {
    boilLength = val;
    phase = BOILING;   // phase will automatically cascade down
  }

  public void setQuenchLen(double val) {
    quenchLength = val;
    phase = BOILING;
    updateJumpDec();
    updateBarrierDec();
  }

  public void setMinBarrier(double val) {
    minBarrierRate = val;
    updateBarrierDec();
  }

  public void setMaxBarrier(double val) {
    // scale barrier rate:
    barrierRate = val*(barrierRate-maxBarrierRate)/maxBarrierRate + val;

    maxBarrierRate = val;
    updateBarrierDec();
  }

  public void setCentreFlag(boolean val) {
    centreFlag = val;
  }

  public void setColourFlag(boolean val) {
    colourFlag = val;
  }

  public void setEyeCandyFlag(boolean val) {
    eyeCandyFlag = val;
  }

  public void reset() {
  // fix this up
    iterations = 0;
    universe.reset();
    updateJumpDec();
    updateBarrierDec();
    phase = BOILING;
    barrierRate = maxBarrierRate;
    maxJump = boilJump;
    nodes = root.getNodes();
  }

  public NodeLayout createNodeLayout() {
    return new FastNodeLayout();
  }

  public EdgeLayout createEdgeLayout() {
    return new FastEdgeLayout();
  }

}