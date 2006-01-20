package org.wilmascope.viewplugin;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleStripArray;
import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.wilmascope.graph.Plane;
import org.wilmascope.view.Colours;
import org.wilmascope.view.NodeView;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Title: WilmaToo Description: Sequel to the ever popular Wilma graph drawing
 * engine Copyright: Copyright (c) 2001 Company: WilmaOrg
 * 
 * @author Tristan Manwaring, adapted from Tim Dwyer
 * @version 1.0
 */

public class CircularNodeView extends NodeView {
	//the plane on which the circle lies
	private Plane p = null;
	
	public CircularNodeView() {
		setTypeName("Circular Node");
	}
	
	/**
	 * @return the Plane the cirle lies on
	 */
	public Plane getPlane() {
		return p;
	}
	
	/**
	 * @param plane the new Plane for the node to lie on 
	 */
	public void setPlane(Plane plane) {
		p = plane;
	}

	protected void setupDefaultMaterial() {
		setupDefaultAppearance(Colours.greyBlueMaterial);
	}

	protected void setupHighlightMaterial() {
		setupHighlightAppearance(Colours.yellowMaterial);
	}

	public void draw() {
		org.wilmascope.graph.Cluster c = ((org.wilmascope.graph.Node) getNode()).getOwner();    
		Vector3f v = p.getNormal();
		AxisAngle4f angle = getAxisAngle4f(new Vector3f(0, 1, 0), v);

		setFullTransform(new Vector3d(1, 1, 1), new Vector3f(c
				.getPosition()), angle);
	}

	/**
	 * The plane this circular node lies on is set when init() is called
	 * the first time.
	 */
	public void init() {
		float radius = getRadius();
		createTubeShape(Cylinder.GENERATE_NORMALS_INWARD, radius);
		createTubeShape(Cylinder.GENERATE_NORMALS, radius);
		
		if(p == null){
			org.wilmascope.graph.Cluster c = ((org.wilmascope.graph.Node) getNode()).getOwner(); 
			p = c.getNodes().getBestFitPlane();
		}
	}

	private void createTubeShape(int inwardNormals, float radius) {
		Cylinder o = new Cylinder(radius, 0.1f /*height*/, inwardNormals, 50,1,getAppearance());
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
		tubeGeometryArray.setCapability(GeometryArray.ALLOW_INTERSECT);
		tubeGeometryArray.setCoordRef3f(gi.getCoordinates());
		tubeGeometryArray.setNormalRef3f(gi.getNormals());
		Appearance redAppearance = new Appearance();
		redAppearance.setMaterial(Colours.redMaterial);
		
		Shape3D tubeShape = new Shape3D(tubeGeometryArray, redAppearance);
		tubeShape.setPickable(true);
		makePickable(tubeShape);
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