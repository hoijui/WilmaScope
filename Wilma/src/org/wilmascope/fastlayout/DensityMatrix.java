package org.wilmascope.fastlayout;

import javax.vecmath.Point3f;
import org.wilmascope.graph.*;

/**
 * <p>Description: Class abstracting the storing of the node densities for FastLayout</p>
 * <p>@author James Cwling</p>
 * <p>@version 0.5</p>
 *
 */

 //
 //
 // move all references to footprint, resolution etc out of fast-layout into here
 //
 //

public class DensityMatrix {

  double[][] matrix;

  double[][] footprint;

  int resolution;

  int footRadius;

  double cutoff;

  // radii before expansion has occurred
  int initWidth, initHeight = 0;

  public DensityMatrix(int width, int height, int resolution, int footprint, double cutoff) {
    this.resolution = resolution;
    initWidth = width;
    initHeight = height;
    this.footRadius = resolution*footprint;
    matrix = new double[width][height];
    this.cutoff = cutoff;
    calcFootprint();
  }

  public void setFootprint(int footprint) {
    footRadius = resolution*footprint;
    calcFootprint();
  }

  public void setRes(int res) {
    footRadius = (footRadius/resolution)*res;
    resolution = res;
    calcFootprint();
  }

  public void setRadius(int val) {
    initWidth = initHeight = val;
    reset();
    System.err.println("Density matrix being reset on radius change!");
  }

  private void calcFootprint() {
    footprint = new double[2*footRadius+1][2*footRadius+1];

    double attenuateScale = cutoff*footRadius*footRadius;

    for(int i = 0; i < footprint.length; i++) {
//      System.err.print("\n[");
      for(int j = 0; j < footprint[i].length; j++) {
        int radSquared = distSquared(footRadius, footRadius, i, j);
  // look into why i need to have the +1 here to use up all the matrix:
        if(radSquared <= footRadius*footRadius+1) { // could get rid of this - just restricts density to within circular footprint
          if(radSquared == 0) radSquared = 1;
          footprint[i][j] = attenuateScale/radSquared;
        }
        else footprint[i][j] = 0;
//        System.err.print(" " + footprint[i][j]);
      }
//      System.err.print(" ]");
    }
  }

  private static int distSquared(int x1, int y1, int x2, int y2) {
    return ((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
  }

  public void setZero() {
    for(int i = 0; i < matrix.length; i++) {
      for(int j = 0; j < matrix[i].length; j++) {
        matrix[i][j] = 0;
      }
    }
  }

  // maybe take a position, not a node, maybe located in a density class with the
  // array
  // should return the density around the node
  // will need to have some proportionality constant, i'll need to experiment to get that
  // the constant can be implemented when placing the densities instead
  //
  // if a position outside the universe is requested, the universe should be expanded to double dimensions
  public double get(Point3f pos) {
    int x = Math.round(pos.x * resolution) + matrix.length/2;
    int y = Math.round(pos.y * resolution) + matrix[0].length/2;

    if (outOfBounds(x,y)) return 0;
    else return matrix[x][y];
  }


  // a little difficulties here with the arrary having only positive indexes
  // check the x+universe.length/2 stuff when resizing happens

  public void set(Point3f pos, float mass) {

    int x = Math.round(pos.x * resolution) + matrix.length/2;
    int y = Math.round(pos.y * resolution) + matrix[0].length/2;

    // check if either of two opposite corners are out of bounds
    while(outOfBounds(x-footRadius, y-footRadius) || outOfBounds(x+footprint.length-1-footRadius, y+footprint[0].length-1-footRadius)) {
      System.err.println("out of bounds attempt at: (" + x + ", " + y + ") with radius " + footRadius);
      expand();
      x = Math.round(pos.x * resolution) + matrix.length/2;
      y = Math.round(pos.y * resolution) + matrix[0].length/2;
    }

    for(int i = 0; i < footprint.length; i++) {
      for(int j = 0; j < footprint[i].length; j++) {
        place(x+i-footRadius, y+j-footRadius, mass*footprint[i][j]);
      }
    }
  }

  private void place(int x, int y, double amount) {
    matrix[x][y] += amount;
  }

  public void set(NodeList nodes) {
    for(nodes.resetIterator(); nodes.hasNext();) {
      set(nodes.nextNode());
    }
  }

  public void set(Node node) {
    set(node.getPosition(), node.getMass());
  }

  public void update(Point3f oldPos, Point3f newPos, Node node) {
    set(oldPos, - node.getMass());
    set(newPos, node.getMass());
  }


  private boolean outOfBounds(int x, int y) {
    return (x < 0 || y < 0 || x >= matrix.length || y >= matrix[0].length);
  }

  private boolean outOfBounds(float x, float y) {
    return outOfBounds(Math.round(x), Math.round(y));
  }


  public void expand() {
    // expand the matrix in some (hopefully) intelligent way
    // the 'dumb' method of simply doubling the dimensions might turn out to be fastest
    // this will ruin time constraints if it happens too frequently
    //
    // check this isn't misaligning densities
    //
    // ideally like to accept a parameter specifying which direction to expand into
    System.err.println("expanding universe...");
    double[][] newMatrix = new double[2*matrix.length][2*matrix.length];
    for(int i = 0; i < matrix.length; i++) {
      for(int j = 0; j < matrix[i].length; j++) {
        newMatrix[i+matrix.length/2][j+matrix.length/2] = matrix[i][j];
      }
    }
    matrix = newMatrix;
  }

  // not sure whether to set all of the current matrix to zero or create a new
  // matrix of the default dimensions
  public void reset() {
    matrix = new double[initWidth][initHeight];
  }

}