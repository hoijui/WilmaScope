package org.wilmascope.viewplugin;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleStripArray;
import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.forcelayout.ForceLayout;
import org.wilmascope.graph.LayoutEngine;
import org.wilmascope.graph.Node;
import org.wilmascope.graph.Plane;
import org.wilmascope.view.ClusterView;
import org.wilmascope.view.Colours;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Title: WilmaToo Description: Sequel to the ever popular Wilma graph drawing
 * engine Copyright: Copyright (c) 2001 Company: WilmaOrg
 * 
 * @author Tim Dwyer
 * @version 1.0
 */

public class ConeClusterView extends ClusterView {
	public ConeClusterView() {
		setTypeName("Planar Cluster");
	}

	protected void setupDefaultMaterial() {
		setupDefaultAppearance(Colours.greyBlueMaterial);
	}

	protected void setupHighlightMaterial() {
		setupHighlightAppearance(Colours.yellowMaterial);
	}

	public void draw() {
		org.wilmascope.graph.Cluster c = (org.wilmascope.graph.Cluster) getNode();    
		float radius = 0;
	    float tmpRadius;
	    for (Node member : c.getAllNodes()) {
	      tmpRadius = c.getPosition().distance(member.getPosition());
	      if (tmpRadius > radius) {
	        radius = tmpRadius;
	      }
	    }
		Plane p = c.getNodes().getBestFitPlane();
		Vector3f norm = new Vector3f();
		Vector3f v = p.getNormal();
		AxisAngle4f angle = getAxisAngle4f(new Vector3f(0, 1, 0), v);

		setFullTransform(new Vector3d(radius, radius, radius), new Vector3f(c
				.getPosition()), angle);
	}

	public void init() {
        // System.out.println("INIT");
		setExpandedView();
		org.wilmascope.graph.Cluster c = (org.wilmascope.graph.Cluster) getNode();
		c.getProperties().getProperty("OrbitalConstraints");
		LayoutEngine l = c.getLayoutEngine();
		int orbits = 0;
		float orbitSep = 1.0f;
		if (l instanceof ForceLayout) {
			ForceLayout fl = (ForceLayout) l;
			orbits = fl.getOrbits();
			orbitSep = fl.getOrbitSeparation();
			for (int i = 1; i <= orbits; i++) {
				float r = (float)i/(float)orbits;
				createTubeShape(Cylinder.GENERATE_NORMALS_INWARD, r);
				createTubeShape(Cylinder.GENERATE_NORMALS, r);
			}
		}

		Cylinder cyl = new Cylinder(1f, 0.02f, Cylinder.GENERATE_NORMALS, 50,1,getAppearance());
		Transform3D t = new Transform3D();
		// t.rotX(Math.PI/2.0);
		TransformGroup g = new TransformGroup(t);
		g.addChild(cyl);
		getTransformGroup().addChild(g);
		makePickable(cyl.getShape(Cylinder.BODY));
		makePickable(cyl.getShape(Cylinder.TOP));
		makePickable(cyl.getShape(Cylinder.BOTTOM));

	}

	private void createTubeShape(int inwardNormals, float radius) {
        // System.out.println("CTS");
		Cylinder o = new Cylinder(radius, 0.02f, inwardNormals, 50,1,getAppearance());
		GeometryStripArray geometry = (GeometryStripArray) o.getShape(
				Cylinder.BODY).getGeometry();
		int[] stripCounts = new int[geometry.getNumStrips()];
		Point3f[] points = new Point3f[geometry.getVertexCount()];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Point3f();
		}
		geometry.getCoordinates(0, points);
		geometry.getStripVertexCounts(stripCounts);
		// rotate so that cylinder stands lengthwise in the z-dimension
		Transform3D rotateTransform = new Transform3D();
		rotateTransform.rotX(Math.PI / 2);
		for (int i = 0; i < points.length; i++) {
			rotateTransform.transform(points[i]);
		}
		GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_STRIP_ARRAY);
		gi.setCoordinates(points);
		gi.setStripCounts(stripCounts);
		NormalGenerator normalGenerator = new NormalGenerator();
		normalGenerator.generateNormals(gi);
		GeometryArray tubeGeometryArray = new TriangleStripArray(points.length,
				GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE
						| GeometryArray.NORMALS, stripCounts);
		tubeGeometryArray.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		tubeGeometryArray.setCoordRef3f(gi.getCoordinates());
		tubeGeometryArray.setNormalRef3f(gi.getNormals());
		Appearance redAppearance = new Appearance();
		redAppearance.setMaterial(Colours.redMaterial);
		Shape3D tubeShape = new Shape3D(tubeGeometryArray, redAppearance);
		Transform3D t = new Transform3D();
		t.rotX(Math.PI / 2.0);
		TransformGroup g = new TransformGroup(t);
		g.addChild(tubeShape);
		getTransformGroup().addChild(g);
	}

	public ImageIcon getIcon() {
		return new ImageIcon(org.wilmascope.images.Images.class
				.getResource("planarcluster.png"));
	}
}
