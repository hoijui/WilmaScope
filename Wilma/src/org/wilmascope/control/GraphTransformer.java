package org.wilmascope.control;

import org.wilmascope.graph.Node;
import org.wilmascope.graph.NodeList;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GraphTransformer {
  public GraphTransformer() {
    random = org.wilmascope.global.RandomGenerator.getRandom();
  }
  public Vector[] kMeansClustering(GraphControl.ClusterFacade cf, int k, int n) {
    if(k < n) {
      throw new IllegalArgumentException("k must be >= n");
    }
    if(n < 1) {
      throw new IllegalArgumentException("n must be >= 1");
    }
    Vector[] clusters = kMeansClustering(cf,k);
    Vector[] largestClusters = new Vector[n];
    int[] sizes = new int[k];
    for(int i=0; i<k; i++) {
      sizes[i] = clusters[i].size();
    }
    Arrays.sort(sizes);
    int limit = sizes[k-n];
    for(int i=0, j=0; i<k && j<n; i++) {
      if(clusters[i].size() >= limit) {
        largestClusters[j++]=clusters[i];
      }
    }
    return largestClusters;
  }

  public Vector[] kMeansClustering(GraphControl.ClusterFacade cf, int k) {
    Vector nodes = new Vector(Arrays.asList(cf.getNodes()));
    if(k > nodes.size()) {
      throw new IllegalArgumentException("k > number nodes in cluster facade");
    }
    Vector[] clusters = new Vector[k];
    Point3f[] clusterBarycenters = new Point3f[k];

    GraphControl.NodeFacade n;
    Point3f p;
    // randomly select starting nodes for clusters
    for(int i = 0; i < k; i++) {
      int r = random.nextInt(nodes.size());
      n = (GraphControl.NodeFacade)nodes.remove(r);
      clusters[i] = new Vector();
      clusters[i].add(n);
      clusterBarycenters[i] = n.getPosition();
    }

    // add nodes to their closest clusters
    float newClusterDistance = Float.MAX_VALUE, d;
    for(int j=0; j < nodes.size();j++) {
      n = (GraphControl.NodeFacade)nodes.get(j);
      p = n.getPosition();
      int newCluster = 0;
      for(int i = 0; i < k; i++) {
        d = distanceSquared(clusterBarycenters[i], p);
        if(d < newClusterDistance) {
          newCluster = i;
          newClusterDistance = d;
        }
      }
      clusters[newCluster].add(n);
    }
    // update the barycenters of the clusters
    for(int i = 0; i < k; i++) {
      clusterBarycenters[i] = getBarycenter(clusters[i]);
    }
    int moved = 0;
    do {
      moved = improveKClusters(k, clusters, clusterBarycenters);
    } while(moved>0);

    return clusters;
  }
  private float distanceSquared(Point3f u, Point3f v) {
    Vector3f vec = new Vector3f();
    vec.sub(u,v);
    return vec.lengthSquared();
  }
  private int improveKClusters(int k, Vector[] clusters, Point3f[] clusterBarycenters) {
    GraphControl.NodeFacade n;
    Point3f p;
    int moved = 0, newCluster;
    float oldDistance, newDistance;
    for(int i = 0; i < k; i++) {
      for(int h = 0; h < clusters[i].size(); h++) {
        n = (GraphControl.NodeFacade)clusters[i].get(h);
        p = n.getPosition();
        oldDistance = distanceSquared(p, clusterBarycenters[i]);
        newCluster = -1;
        for(int j = 0; j < k; j++) {
          if(i==j) continue;
          newDistance = distanceSquared(p, clusterBarycenters[j]);
          if(newDistance < oldDistance) {
            oldDistance = newDistance;
            newCluster = j;
          }
        }
        if(newCluster >= 0) {
          clusters[i].remove(n);
          clusters[newCluster].add(n);
          moved++;
        }
      }
    }
    // update the barycenters of the clusters
    for(int i = 0; i < k; i++) {
      clusterBarycenters[i] = getBarycenter(clusters[i]);
    }
    return moved;
  }
  private Point3f getBarycenter(Vector nodes) {
    Point3f barycenter = new Point3f();
    for(Iterator i = nodes.iterator(); i.hasNext();) {
      barycenter.add(((GraphControl.NodeFacade)i.next()).getPosition());
    }
    barycenter.scale(1f/(float)nodes.size());
    return barycenter;
  }
  Random random;
}