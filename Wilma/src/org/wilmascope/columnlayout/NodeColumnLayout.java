package org.wilmascope.columnlayout;

import org.wilmascope.forcelayout.NodeForceLayout;
import javax.vecmath.Vector3f;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class NodeColumnLayout extends NodeForceLayout {
    public NodeColumnLayout(int level) {
        setLevel(level);
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getStratum() {
        return level;
    }
    public void addForce(Vector3f f) {
        ((NodeForceLayout)getNode().getOwner().getLayout()).addForce(f);
    }
    public void subForce(Vector3f f) {
        ((NodeForceLayout)getNode().getOwner().getLayout()).subForce(f);
    }
    int level;
}