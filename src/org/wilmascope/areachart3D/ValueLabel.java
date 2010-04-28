/*
 * The following source code is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Christine Wu however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * WilmaScope as an influence and inform me(sainttale@hotmail.com)
 * if you do anything really cool with it.
 *
 *
 * -- Christine, 2003
 */
package org.wilmascope.areachart3D;

/**
 * @author Christine
 *
 */
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import java.awt.*;
import javax.vecmath.*;
/**Class ValueLabel creates the labels for each marking lines 
 */
public class ValueLabel extends TransformGroup {
	private TransformGroup[] TGArray;
	private Text2D[] textArray;
	private Color3f color = new Color3f(1, 1, 1);
	private BoundingSphere bounds =
		new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	private TransformGroup billBoardGroup;
	/**@lineValue The gap between 2 marking lines
	 * @lineNum The number of the marking lines
	 * @height The height of the chart
	 */
	public ValueLabel(int lineValue, int lineNum, float height) {
		// billBoard makes the label always facing the user
		billBoardGroup = new TransformGroup();
		billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Billboard billboard = new Billboard(billBoardGroup);
		billboard.setSchedulingBounds(bounds);
		this.addChild(billBoardGroup);
		this.addChild(billboard);
		addText(lineValue, lineNum, height);
		for (int i = 0; i < lineNum; i++)
			billBoardGroup.addChild(TGArray[i]);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}
	private void addText(int lineValue, int lineNum, float height) {
		float y = height / lineNum;
		int value = lineValue;
		TGArray = new TransformGroup[lineNum];
		textArray = new Text2D[lineNum];
		Transform3D trans = new Transform3D();
		for (int i = 0; i < lineNum; i++) {
			TGArray[i] = new TransformGroup();
			textArray[i] = new Text2D("" + value, color, "Dummy", 36, Font.BOLD);
			//calculate the text width
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Font font = new java.awt.Font("Dummy", Font.PLAIN, 20);
			FontMetrics metrics = toolkit.getFontMetrics(font);
      float width = metrics.stringWidth("" + value) * 1f / 260f;
      float txtHeight = metrics.getHeight() * 1f / 260f;
			trans.setTranslation(new Vector3f(-width, y-txtHeight/3f, 0));
			trans.setScale(15d / 36d);
			TGArray[i].setTransform(trans);
			y += height / lineNum;
			TGArray[i].addChild(textArray[i]);

			value += lineValue;

		}
	}
	/**Sets the position of the plane 
	 */
	public void setPosition(Point3f position) {
		Transform3D trans = new Transform3D();
		trans.setTranslation(new Vector3f(position.x, position.y, position.z));
		this.setTransform(trans);
	}

}
