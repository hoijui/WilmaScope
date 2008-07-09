package org.wilmascope.areaChart3D;

/*
 * The following source code is distributed under the terms of the GNU Lesser General Public License
 * (LGPL - http://www.gnu.org/copyleft/lesser.html).
 *
 * As usual we distribute it with no warranties and anything you chose to do
 * with it you do at your own risk.
 *
 * Copyright for this work is retained by Christine Wu however it may be used or modified to work as part of
 * other software subject to the terms of the LGPL.  I only ask that you cite
 * areaChart3D as an influence and inform me(sainttale@hotmail.com)
 * if you do anything really cool with it.
 *
 * -- Christine, 2003
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.wilmascope.areaChart3D.ChartAreaShape;
import org.wilmascope.areaChart3D.MarkLine;
import org.wilmascope.areaChart3D.ValueLabel;
import org.wilmascope.areaChart3D.ValueLine;
import org.wilmascope.areaChart3D.ZLabel;

import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.universe.SimpleUniverse;
/** 
 * Create a 3D Area Chart: for example money invested into certain business xCategories at certain time.
 * 
 * @author Christine
 *
 */
public class Chart extends Frame {
	// company name
	private String[] xLabels;
	//zLabels Vector
	private Vector<String> zLabels = new Vector<String>();
	//figures
	private float[][] data;
	//the biggest number in the data array
	private float max = 0;
	//transformGroup to place the middle point of chart to the origin
	private TransformGroup objTrans = new TransformGroup();
	//transformGroup to rotate the scene graph
	private TransformGroup objRot = new TransformGroup();
	//transformGroup to translate and zoom the scene graph
	private TransformGroup objZoom = new TransformGroup();
	// array for the HSB value
	private float hsbvals[] = new float[3];
	// the amount of Hue increased each time a new ChartAreaShape created
	private float deltaHue = 0;
	// index for the color list
	private int colorIndex = 0;
	private Vector colorList = new Vector();
	// marking lines that indicate the value
	private ValueLine lines;
	// figure labels
	private ValueLabel valueLabels;
	//number of marking lines
	private int lineNum;
	private int lineValue;
	//the height of the chart 
	private float height;
	// the width of the chart
	private float width = 1f;
	//thick of the xCategories
	private float thick = 0.05f;
	//the length of the chart
	private float length;
	// the transparent moving plane
	private Plane plane;
	//the root branch Group
	private BranchGroup objRoot = new BranchGroup();
	//branchGroup for the text appear when you clicked one of the sector
	private BranchGroup textBG;
	private Canvas3D canvas3D;
	private BoundingSphere bounds;
	// the array for xCategories
	private XCategories[] xCategories;
	// branchGroup for the company name labels
	private BranchGroup nameBG;
	// the sector that you clicked
	private ChartAreaShape sectorPicked;
	/** Initializes the chart using a file containing the data
	 * <p>
	 * file format is:
	 * <p>
	 * first line: list of column names separated by '|'
	 * subsequent lines: row heading|float data values separated by '|'
	 * <p>
	 * parser is not very robust so make sure file is correct.
	 * @param FileName Name of the data file
	 */
	public Chart(String FileName) throws IOException {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
        e.getWindow().dispose();
      }
		});
		//read the data from file
		readDataFile(FileName);
		initialize();
	}
	/** Initializes the chart using array and Vector
	 *  @param xLabels Array contains the labels for the x-axis
	 *  @param data Two dimention array contains the data.
	 *             <br>     i\j   zLabel1 zLabel2 zLabel3
	 *             <br>  zLabel1    X       X       X
	 *             <br>  zLabel2    X       X       X
	 *             <br>  zLabel3    X       X       X
	 *  @param zLabels Array contains the labels for the z-axis
	 */
	public Chart(String[] xLabels, float[][] data, String[] zLabels) {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
        dispose();
				//System.exit(0);
			}
		});

		this.xLabels = xLabels;
		this.data = data;
		this.zLabels = new Vector(Arrays.asList(zLabels));
		max = 0;
		for (int i = 0; i < xLabels.length; i++) {
			for (int j = 0; j < zLabels.length; j++) {
				if (data[i][j] > max)
					max = data[i][j];
			}
		}
		initialize();
	}
	//initialize the transformGroup and simple universe
	private void initialize() {
		initColorList();

		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

		objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objZoom.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objZoom.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		add("Center", canvas3D);

		BranchGroup scene = createSceneGraph();
		scene.compile();

		// SimpleUniverse is a Convenience Utility class
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

		// This moves the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		simpleU.getViewingPlatform().setNominalViewingTransform();

		canvas3D.getView().getPhysicalBody().setLeftEyePosition(
			new Point3d(-0.003, 0.0, 0.0));
		canvas3D.getView().getPhysicalBody().setRightEyePosition(
			new Point3d(0.003, 0.0, 0.0));
		simpleU.addBranchGraph(scene);
	}
	private void readDataFile(String FileName) throws IOException {
		File inFile = new File(FileName);
		String line = new String();
		Vector temp = new Vector();
		int companyNum = 0;
		StringTokenizer stTok;
		//first line of the file is the companies' names,seperate by space
		FileReader in = new FileReader(inFile);
		LineNumberReader lr = new LineNumberReader(in);
		line = lr.readLine();

		if (line != null) {
			stTok = new StringTokenizer(line, "|");
			companyNum = stTok.countTokens();
			xLabels = new String[companyNum];
			int count = 0;
			while (stTok.hasMoreTokens()) { //get the company names
				xLabels[count] = stTok.nextToken();
				count++;

			}

		}
		line = lr.readLine();
		// the next lines contains dates and data
		//    date   data1 data2 data3  ...
		//   200208 12298 86380 119575 1246665 72831 531047    
		while (line != null) {
			float[] dataArray = new float[companyNum];
			stTok = new StringTokenizer(line, "|");
			if (stTok.countTokens() - 1 != companyNum) {
				throw new IOException(
					"Number of data values did not match number of column headings in: "
						+ inFile
						+ " line: "
						+ lr.getLineNumber());
			}
			zLabels.add(stTok.nextToken());
			int dataCount = 0;
			while (stTok.hasMoreTokens()) {
				dataArray[dataCount] = Float.parseFloat(stTok.nextToken());
				if (dataArray[dataCount] > max)
					max = dataArray[dataCount];
				dataCount++;
			}
			temp.add(dataArray);
			line = lr.readLine();
		}
		int monthNum = lr.getLineNumber() - 1;
		data = new float[companyNum][monthNum];
		for (int i = 0; i < temp.size(); i++)
			for (int j = 0; j < companyNum; j++)
				data[j][i] = ((float[]) temp.get(i))[j];
		lr.close();
		in.close();
	}
	public BranchGroup createSceneGraph() {

		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Set up the background
		Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);
		Background bg = new Background(bgColor);
		bg.setApplicationBounds(bounds);
		//add the x Labels to the scene graph        	
		Transform3D trans = new Transform3D();
		float zCoord = 0;
		xCategories = new XCategories[xLabels.length];
		//  create the xCategories and add it the the scene graph
		for (int i = 0; i < xLabels.length; i++) {
			xCategories[i] =
				new XCategories(
					data[i],
					zCoord,
					width,
					thick,
					max,
					generateNewColor(),
					xLabels[i]);
			objTrans.addChild(xCategories[i]);
			zCoord -= 0.05f;

		}
		//calulate the value on each line
		calculateLineValue(max);
		height = ((float) lineValue * lineNum) / max;
		length = -zCoord;
		//create the marking lines 
		lines = new ValueLine(lineNum, length, height, 1f);
		objTrans.addChild(lines);
		//create the value labels 
		valueLabels = new ValueLabel(lineValue, lineNum, height);
		objTrans.addChild(valueLabels);

		//create the companies name label 
		NameLabel nameLabel = new NameLabel(xCategories, 0.05f, width);
		nameBG = new BranchGroup();
		nameBG.addChild(nameLabel);
		nameBG.setCapability(BranchGroup.ALLOW_DETACH);
		objTrans.addChild(nameBG);

		//create the dates marking line and dates labels 
		objTrans.addChild(new MarkLine(zLabels.size(), length, width, 0.04f));
		objTrans.addChild(new ZLabel(zLabels, length, width));
		plane = new Plane(lineNum, length, height);
		objTrans.addChild(plane);
		//add the key board control to the scenegraph so that you can move the plane
		//using -> and <-   
		KeyboardCtrl movePlane = new KeyboardCtrl(this);
		movePlane.setSchedulingBounds(bounds);
		objTrans.addChild(movePlane);

		// add the behaviors to the scenegraph so that it can be rotate in y axis ,zoom and translate
		RotYAxis rotY = new RotYAxis(this);
		MouseZoom mouseZoom = new MouseZoom(objZoom);
		MouseTranslate mouseTranslate = new MouseTranslate(objZoom);
		mouseZoom.setSchedulingBounds(bounds);
		rotY.setSchedulingBounds(bounds);
		mouseTranslate.setSchedulingBounds(bounds);
		objRoot.addChild(rotY);
		objRoot.addChild(mouseZoom);
		objRoot.addChild(mouseTranslate);

		//move the center point of the chart to the origin 
		trans.setTranslation(new Vector3f(-0.5f, -0.5f, length / 2));
		objTrans.setTransform(trans);
		objRot.addChild(objTrans);
		Transform3D rotPI = new Transform3D();
		rotPI.rotY(-Math.PI / 3);
		objRot.setTransform(rotPI);

		objZoom.addChild(objRot);
		trans.setTranslation(new Vector3f(0, 0, -2.2f));
		objZoom.setTransform(trans);
		objRoot.addChild(objZoom);
		objRoot.addChild(new PickBehavior(objRoot, canvas3D, bounds, this));
		// add some lights 
		PointLight point = new PointLight();
		point.setPosition(0, 0, 1);
		point.setInfluencingBounds(bounds);
		AmbientLight amb = new AmbientLight();
		amb.setInfluencingBounds(bounds);
		objRoot.addChild(point);
		objRoot.addChild(amb);

		return objRoot;
	}
	private void initColorList() {
		colorList.add(Color.red);
		colorList.add(Color.blue);
		colorList.add(Color.green);
		colorList.add(new Color(255, 248, 187));
		colorList.add(new Color(102, 0, 0));

	}
	private Color3f generateNewColor() {
		// change the hue values of the colors in the colorList so that a new color can be 
		//generated
		Color orgColor = (Color) colorList.get(colorIndex);
		Color.RGBtoHSB(
			orgColor.getRed(),
			orgColor.getBlue(),
			orgColor.getGreen(),
			hsbvals);
		hsbvals[0] += deltaHue;
		int rgb = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
		rgb &= 0x00ffffff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		Color3f newColor =
			new Color3f((float) r / 255, (float) g / 255, (float) b / 255);
		colorIndex++;
		if (colorIndex >= colorList.size()) {
			colorIndex = 0;
			deltaHue += 0.8f / xLabels.length * colorList.size();
		}

		return newColor;
	}

	private void calculateLineValue(float max) {
		float value = max;
		int powerBy = 0;
		while (value > 10) {
			powerBy++;
			value /= 10;
		}

		lineValue = (int) Math.pow(10, powerBy);
		while ((max / lineValue + 1) < 5&&lineValue>1)
			lineValue = lineValue / 2;
		while ((max / lineValue + 1) > 9)
			lineValue = lineValue * 2;
		lineNum = (int) Math.ceil((float) max / lineValue);

	}
	/**@return The transformGroup for scene graph rotation
	 */
	public TransformGroup getRotTG() {
		return objRot;
	}
	/**@return The value labels
	 */
	public ValueLabel getValueLabels() {
		return valueLabels;
	}
	/** @return the marking lines 
	*/
	public ValueLine getValueLine() {
		return lines;
	}
	/**@return The length of the chart
	 */
	public float getChartLength() {
		return length;
	}
	/**@return The width of the chart
	 */
	public float getChartWidth() {
		return width;
	}

	public TransformGroup getObjTrans() {
		return objTrans;
	}
	/** @return The transparent moving plane
	 */
	public Plane getPlane() {
		return plane;
	}
	/**@return The Vector of zLabels
	 */
	public Vector<String> getCalendar() {
		return zLabels;
	}
	/** show the money invested into the selected sector at certain 
	 */
	public void showFigure() {
		//detach the original Text2D	
		if (sectorPicked != null) {
			if (textBG != null) {
				textBG.detach();
				textBG = new BranchGroup();
				textBG.setCapability(BranchGroup.ALLOW_DETACH);
			} else {
				textBG = new BranchGroup();
				textBG.setCapability(BranchGroup.ALLOW_DETACH);
			}
			//calculate the date     
			int index =
				(int) (plane.getPlanePosition() / width * (zLabels.size() - 1));
			//get the amount of money  
			float value = sectorPicked.getValue(index);
			//create two Text2D objects for the figure. One for the front, and the other for the back
			Text2D figureFront =
				new Text2D("" + value, new Color3f(1, 1, 1), "Dummy", 36, Font.BOLD);
			Text2D figureBack =
				new Text2D("" + value, new Color3f(1, 1, 1), "Dummy", 36, Font.BOLD);

			Text2D dateFront =
				new Text2D(
					(String) zLabels.get(index),
					new Color3f(1, 1, 1),
					"Dummy",
					36,
					Font.BOLD);
			Text2D dateBack =
				new Text2D(
					(String) zLabels.get(index),
					new Color3f(1, 1, 1),
					"Dummy",
					36,
					Font.BOLD);

			Text2D nameFront =
				new Text2D(
					sectorPicked.getSectorName(),
					new Color3f(1, 1, 1),
					"Dummy",
					36,
					Font.BOLD);
			Text2D nameBack =
				new Text2D(
					sectorPicked.getSectorName(),
					new Color3f(1, 1, 1),
					"Dummy",
					36,
					Font.BOLD);

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Font font = new java.awt.Font("Dummy", Font.BOLD, 36);
			FontMetrics metrics = toolkit.getFontMetrics(font);
			float fontWidth = metrics.getHeight() * 1f / 256f;

			TransformGroup frontTG = new TransformGroup();
			Transform3D trans1 = new Transform3D();
			trans1.rotY(Math.PI / 2);
      trans1.setScale(0.5);
			trans1.setTranslation(
				new Vector3f(
					plane.getPlanePosition() + 0.001f,
					height,
					sectorPicked.getZCoord()));
			frontTG.setTransform(trans1);
			frontTG.addChild(figureFront);
			//attach the company name
			TransformGroup nameTG = new TransformGroup();
			nameTG.addChild(nameFront);
			Transform3D nameTrans = new Transform3D();
			nameTrans.setTranslation(new Vector3f(0, fontWidth, 0));
			nameTG.setTransform(nameTrans);
			frontTG.addChild(nameTG);
			//attach the date
			TransformGroup dateTG = new TransformGroup();
			dateTG.addChild(dateFront);
			Transform3D dateTrans = new Transform3D();
			dateTrans.setTranslation(new Vector3f(0, 2 * fontWidth, 0));
			dateTG.setTransform(dateTrans);
			frontTG.addChild(dateTG);
			//labels on the back
			TransformGroup backTG = new TransformGroup();
			Transform3D trans2 = new Transform3D();
			trans2.rotY(-Math.PI / 2);
			trans2.setTranslation(
				new Vector3f(
					plane.getPlanePosition() - 0.001f,
					height,
					sectorPicked.getZCoord() - 0.05f));
			backTG.setTransform(trans2);
			backTG.addChild(figureBack);

			TransformGroup nameTG1 = new TransformGroup();
			nameTG1.addChild(nameBack);
			Transform3D nameTrans1 = new Transform3D();
			nameTrans1.setTranslation(new Vector3f(0, fontWidth, 0));
			nameTG1.setTransform(nameTrans1);
			backTG.addChild(nameTG1);

			TransformGroup dateTG1 = new TransformGroup();
			dateTG1.addChild(dateBack);
			Transform3D dateTrans1 = new Transform3D();
			dateTrans1.setTranslation(new Vector3f(0, 2 * fontWidth, 0));
			dateTG1.setTransform(dateTrans1);
			backTG.addChild(dateTG1);

			textBG.addChild(frontTG);
			textBG.addChild(backTG);
			objTrans.addChild(textBG);

		}
	}
	/** 
	 * Set the sector user picked
	 */
	public void setSectorPicked(ChartAreaShape sectorPicked) {
		this.sectorPicked = sectorPicked;
	}
	/** sort the xCategories according to the money invest in the xCategories at certain month
	 */
	public void sort() {
		//detach all the xCategories
		Enumeration e = objTrans.getAllChildren();
		while (e.hasMoreElements()) {
			Object b = e.nextElement();
			if (b instanceof XCategories)
				 ((XCategories) b).detach();
			nameBG.detach();
		}
		// calculate the date

		int index =
			(int) (plane.getPlanePosition() / width * (zLabels.size() - 1));
		//sort the xCategories
		for (int i = 0; i < xLabels.length; i++)
			for (int j = xLabels.length - 1; j > i; j--) {
				if (data[j - 1][index] > data[j][index]) {
					float T[] = data[j - 1];
					data[j - 1] = data[j];
					data[j] = T;

					XCategories temp = xCategories[j - 1];
					xCategories[j - 1] = xCategories[j];
					xCategories[j] = temp;
				}

			}
		//reattach the xCategories to the scene graph    
		float zCoord = 0;
		for (int i = 0; i < xLabels.length; i++) {
			xCategories[i].setZCoord(zCoord);
			objTrans.addChild(xCategories[i]);
			zCoord -= 0.05f;
		}
		NameLabel nameLabel = new NameLabel(xCategories, thick, width);
		nameBG = new BranchGroup();
		nameBG.addChild(nameLabel);
		nameBG.setCapability(BranchGroup.ALLOW_DETACH);
		objTrans.addChild(nameBG);

	}
}
