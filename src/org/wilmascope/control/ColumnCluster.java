package org.wilmascope.control;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import org.wilmascope.forcelayout.*;
import org.wilmascope.columnlayout.*;
import org.wilmascope.viewplugin.TaperedEdgeView;
import org.wilmascope.viewplugin.TubeNodeView;
import org.wilmascope.viewplugin.ColumnClusterView;
import org.wilmascope.view.ViewManager;
import org.wilmascope.view.Colours;
public class ColumnCluster {
    public ColumnCluster(String label, GraphControl.ClusterFacade root, float initValue, float bottomRadius, int initLevel) {
        this.root = root;
        this.label = label;
        lastValue = this.initValue = initValue;
        try {
            ViewManager.getInstance().getClusterViewRegistry().setDefaultView("Column Cluster");
        } catch (Exception e) {
            e.printStackTrace();
        }
        column = root.addNewCluster();
        column.setLevelConstraint(0);
        column.setLabel(label);
        this.level = initLevel;
        //layout = new ColLayout(level);
        if(columnStyle == FORCECOLUMNS || columnStyle == DOTCOLUMNS) {
          layout = new ColTubeLayout(level);
        } else if (columnStyle == WORMS) {
          layout = new FrcLayout();
        }
        initTopRadius = lastTopRadius = bottomRadius;
    }
    interface Layout {
        public GraphControl.NodeFacade addNode(float radius);
        int getNextLevel();
    }
    class ColTubeLayout implements Layout{
        ColTubeLayout(int initLevel) {
            ColumnLayout l = new ColumnLayout(column.getCluster());
            column.setLayoutEngine(l);
            l.setBaseLevel(initLevel);
        }
        public GraphControl.NodeFacade addNode(float radius){
            GraphControl.NodeFacade n = column.addNode("Tube");
            ((TubeNodeView)n.getView()).setEndRadii(lastTopRadius,radius);
            System.out.println("n.getRadius()="+n.getRadius());
            //n.setRadius(n.getRadius()*radius);
            n.setColour(102f/255f, 255f/255f, 51f/255f);
            return n;
        }
        public int getNextLevel() {
            return ((org.wilmascope.columnlayout.ColumnLayout)column.getCluster().getLayoutEngine()).getNextLevel();
        }
    }
    class ColLayout implements Layout{
        ColLayout(int initLevel) {
            ColumnLayout l = new ColumnLayout(column.getCluster());
            column.setLayoutEngine(l);
            l.setBaseLevel(initLevel);
            lastTopNode = topNode = addNode(initTopRadius);
        }
        public GraphControl.NodeFacade addNode(float radius){
            GraphControl.NodeFacade n = column.addNode();
            n.hide();
            n.setRadius(n.getRadius()*radius);
            n.setColour(211f/255f, 199f/255f, 182f/255f);
            if(lastTopNode!=null) {
                TaperedEdgeView.setBottomRadius(lastTopRadius);
                TaperedEdgeView.setTopRadius(radius);
                GraphControl.EdgeFacade e = column.addEdge(lastTopNode, n, "Tapered Edge");
                e.setColour(211f/255f, 199f/255f, 182f/255f);
            }
            return n;
        }
        public int getNextLevel() {
            return ((org.wilmascope.columnlayout.ColumnLayout)column.getCluster().getLayoutEngine()).getNextLevel()-1;
        }
    }
    class FrcLayout implements Layout{
        FrcLayout() {
            ((ForceLayout)column.getLayoutEngine()).addForce(new Origin(15f));
            ((ForceLayout)column.getLayoutEngine()).addForce(new Spring(15f));
            //lastTopNode = topNode = addNode(initTopRadius);
        }
        public GraphControl.NodeFacade addNode(float radius){
            GraphControl.NodeFacade n = column.addNode();
            n.setLevelConstraint(level++);
            n.setRadius(n.getRadius()*radius);
            //n.setColour(211f/255f, 199f/255f, 182f/255f);
            n.setColour(102f/255f, 255f/255f, 51f/255f);
            if(lastTopNode!=null) {
                TaperedEdgeView.setBottomRadius(lastTopRadius);
                TaperedEdgeView.setTopRadius(radius);
                GraphControl.EdgeFacade e = column.addEdge(lastTopNode, n, "Tapered Edge");
                e.setRelaxedLength(0.001f);
                //e.setColour(211f/255f, 199f/255f, 182f/255f);
            e.setColour(102f/255f, 255f/255f, 51f/255f);
            }
            return n;
        }
        public int getNextLevel() {
            return level-1;
        }
    }

    public int getNextLevel() {
        return layout.getNextLevel();
    }
    public GraphControl.NodeFacade addNode(float value) {
        float topRadius = initTopRadius * value/initValue;
        topNode = layout.addNode(topRadius);
        ((ColumnClusterView)column.getCluster().getView()).setPositionRef(topNode.getPosition());
        lastTopRadius = topRadius;
        lastTopNode = topNode;
        lastValue = value;
        //((ColumnClusterView)column.getCluster().getView()).setLabelLevel(getNextLevel());
        return topNode;
    }
    public GraphControl.NodeFacade addNode() {
      return addNode(lastValue);
    }
    public GraphControl.NodeFacade getTopNode() {
      return topNode;
    }
    public static void setColumnStyle(int style) {
      columnStyle = style;
    }
    public static int getColumnStyle() {
      return columnStyle;
    }
    public static final int DOTCOLUMNS = 1;
    public static final int FORCECOLUMNS = 2;
    public static final int WORMS = 3;
    static int columnStyle = DOTCOLUMNS;

    GraphControl.ClusterFacade root;
    GraphControl.ClusterFacade column;
    float lastTopRadius, initTopRadius;
    GraphControl.NodeFacade topNode, lastTopNode;
    float initValue, lastValue;
    String label;
    int level = 0;
    Layout layout;
}