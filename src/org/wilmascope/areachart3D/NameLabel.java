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
 * @author star
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import java.awt.*;
import javax.vecmath.*;
/** Class NameLabel creates the labels for the names of the sectors 
 */
public class NameLabel extends TransformGroup {
	private Color3f color = new Color3f(1, 1, 1);
	/**@param sectors Array of sectors 
	 * @param thick The thick of each sector
	 * @param width the width of the chart
	 */
	public NameLabel(XCategories[] sectors, float thick, float width) {
		float z = 0;
		//create labels at right and left sides
		for (int i = 0; i < sectors.length; i++) {
			TransformGroup TG1 = new TransformGroup();
			TransformGroup TG2 = new TransformGroup();
			Transform3D rot1 = new Transform3D();
			Transform3D rot2 = new Transform3D();
			rot1.rotY(Math.PI / 2);
			rot2.rotX(-Math.PI / 2);
			rot2.mul(rot2, rot1);
			rot2.setScale(10.0 / 36.0);
			TG1.setTransform(rot2);
			Text2D text =
				new Text2D(sectors[i].getSectorName(), color, "Dummy", 36, Font.PLAIN);
			TG1.addChild(text);
			Transform3D trans1 = new Transform3D();
			trans1.setTranslation(new Vector3f(width, 0, z));
			TG2.setTransform(trans1);
			TG2.addChild(TG1);
			this.addChild(TG2);
			z -= thick;

		}
		z = -thick;
		for (int i = 0; i < sectors.length; i++) {
			TransformGroup TG1 = new TransformGroup();
			TransformGroup TG2 = new TransformGroup();
			Transform3D rot1 = new Transform3D();
			Transform3D rot2 = new Transform3D();
			rot1.rotZ(-Math.PI / 2);
			rot2.rotY(-Math.PI / 2);
			rot2.mul(rot2, rot1);
			rot2.setScale(10.0 / 36.0);
			TG1.setTransform(rot2);
			Text2D text =
				new Text2D(sectors[i].getSectorName(), color, "Dummy", 36, Font.PLAIN);
			TG1.addChild(text);
			Transform3D trans1 = new Transform3D();
			trans1.setTranslation(new Vector3f(0, 0, z));
			TG2.setTransform(trans1);
			TG2.addChild(TG1);
			this.addChild(TG2);
			z -= thick;

		}

	}
}
