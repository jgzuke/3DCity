package com.ThreeDCity;

import java.util.ArrayList;

import com.ThreeDCity.Objects.Panel;
import com.ThreeDCity.Objects.Point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

/*
 * Draws all objects
 */
public final class Graphics extends View
{
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	protected double zoom = 5;
	protected double distanceFromPanel = 5;
	// distance you are from the panel
	private double viewSize = 0.005;
	// multiply screenSize by viewSize to get panel size
	private double testing = 50;
	// factor to multiply points by to get edge of player view drawn at edge o screen
	// at rotToTop etc you should draw edge of screen
	
	
	
	private ViewRotations v;
	double screenWidth, screenHeight;
	private Controller control;
	private Paint paint;
	
	public Graphics(Context contextSet, Controller controlSet, double [] dimensions)
	{
		super(contextSet);
		
		screenWidth = dimensions[0];
		screenHeight = dimensions[1];
		control = controlSet;
		paint = new Paint();
	}
	protected void frameCall()
	{
		invalidate();
	}
	/**
	 * draws every object as well as the ground in the distance
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		v =  getView();
		//	(screenHeight/2) = Math.tan(v.rotToPanelTop)*v.distanceFromPanel*testing
		//	testing = (screenHeight)/(2*Math.tan(v.rotToPanelTop)*v.distanceFromPanel)
		testing = (screenHeight)/(2 * Math.tan((v.topRot-v.bottomRot)/2)*v.distanceFromPanel);
		testing = (screenWidth)/(2 * Math.tan((v.rightRot-v.leftRot)/2)*v.distanceFromPanel);
		paint.setColor(Color.WHITE);
		g.drawRect(0, 0, (int)screenWidth, (int)screenHeight, paint);
		paint.setColor(Color.GREEN);
		g.drawRect(0, (int)(screenHeight/2)+(int)(Math.tan(v.pRotation[1])*v.distanceFromPanel*testing), (int)screenWidth, (int)screenHeight, paint);
		paint.setColor(Color.GRAY);
		g.translate((int)(screenWidth/2), (int)(screenHeight/2));
		ArrayList<Panel> panels = (ArrayList<Panel>)control.objects.panels.clone();
		int [] orderToDraw = orderPanels(panels);
		for(int i = 0; i < panels.size(); i++)
		{
			drawPanel(panels.get(orderToDraw[i]), g);
		}
	}
	/**
	 * layers panels to be drawn on top of each other
	 * @return which index will be drawn first, second etc.
	 */
	protected int[] orderPanels(ArrayList<Panel> panels)
	{
		int [] orderToDraw = new int[panels.size()];
		int [] distances = new int[panels.size()];
		double [] playerPos = control.player.getLocation();
		for(int i = 0; i < panels.size(); i++)
		{
			distances[i] = (int) Math.sqrt(	Math.pow(panels.get(i).points[4][0]-playerPos[0], 2)+	// X-Direction
											Math.pow(panels.get(i).points[4][1]-playerPos[1], 2)+	// Y-Direction
											Math.pow(panels.get(i).points[4][2]-playerPos[2], 2));	// Z-Direction
		}
		for(int i = 0; i < panels.size(); i++)	// first elements should be farthest, so drawn overtop
		{
			int index = 0;
			int dist = 0;
			for(int j = 0; j < panels.size(); j++)	// check every elements left
			{
				for(int k = 0; k < i; k++)
				{
					if(orderToDraw[k]==j) j++;			// if this index is in sorted list skip it
				}
				if(j < panels.size())					// make sure we didn't just skip the last index
				{
					if(distances[j]>dist)		// if this object farther away
					{
						index = j;
						dist = distances[j];
					}
				}
			}
			orderToDraw[i]=index;
		}
		return orderToDraw;
	}
	/**
	 * draws panel sent to function
	 * @param panel the panel to draw
	 */
	protected void drawPanel(Panel rawPanel, Canvas g)
	{
		int[][] panel = rawPanel.points.clone();			// fixes panel so it fits on screen
		ArrayList<double[]> p = getPointSet(panel);	// and returns rotations to points
		if(p.size() > 0)							// if panel was on screen
		{
			Path path = new Path();
			int num = p.size();
			path.moveTo((int)p.get(num-1)[0], (int)p.get(num-1)[1]);// start at last corner
			for(int i = 0; i < num; i++)
			{
				path.lineTo((int)p.get(i)[0], (int)p.get(i)[1]); // make lines to each corner
			}
			paint.setStyle(Style.STROKE);
			paint.setColor(rawPanel.color);
			g.drawPath(path, paint);						// draws polygon
		}
	}
	/**
	 * returns coordintes of points on panel, if panel not visible returns null
	 * @param rotations	the rotations to fix
	 * @param view		the view to fit rotations into
	 */
	protected ArrayList<double[]> getPointSet(int[][] panelSet)
	{
		double [][] panelSetDouble = new double[5][3];
		for(int i = 0 ; i < 5; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				panelSetDouble[i][j] = panelSet[i][j];
			}
		}
		PanelWithVectors panel = new PanelWithVectors(panelSetDouble, v, this);
		ArrayList<double[]> p = new ArrayList<double[]>();
		for(int i = 0; i < 8; i++)
		{
			if(panel.points[i] != null)
			{
				p.add(getScreenPoint(panel.points[i].location));
			}
		}
		return p;
	}
	/**
	 * 
	 * @param pos
	 */
	protected double [] relativeCoordinates(double [] pos)
	{
		double xo = pos[0]-control.player.x;
		double yo = pos[1]-control.player.y;
		double zo = pos[2]-control.player.z;
		// rotate around origin by control.player rotations
		double hRot = control.player.hRotation; // angle
		// rotate horizontally
		double x1 = (Math.cos(hRot)*xo) - (Math.sin(hRot)*yo);
		double y = (Math.sin(hRot)*xo) + (Math.cos(hRot)*yo);
		double zRot = control.player.zRotation; // angle
		// rotate vertically
		double x = (Math.cos(zRot)*x1) - (Math.sin(zRot)*zo);
		double z = (Math.sin(zRot)*x1) + (Math.cos(zRot)*zo);
		
		double [] coordinates = {x, y, z};
		return coordinates;
	}
	/**
	 * takes an z, x, y array, returns and z, y array of a point on the screen, null if off screen
	 * @param pos	x, y, z of point to project
	 * @return		the points screen coordinates, or null if doesnt work
	 */
	protected double [] getScreenPoint(double [] pos)
	{
		double [] coordinates = relativeCoordinates(pos);
		double [] ratios = {coordinates[1]/coordinates[0], coordinates[2]/coordinates[0]};
		double [] screenPoint = {testing*distanceFromPanel*ratios[0], 
								testing*distanceFromPanel*ratios[1]};
		if(coordinates[0]<0.001) return null;	// if point behind player, or very very close
		return screenPoint;
	}
	/**
	 * returns whether the rotation set is in players view
	 * @param rotationSet	the rotation set to check
	 * @param view			the view to check if it fits in
	 * @return				whether it fits in players view
	 */
	protected boolean pointOnScreen(double[] coordinates)
	{
		double [] pCoordinates = control.player.getLocation();
		double [] rotations = new double[2];
		double xDif = (coordinates[0]-pCoordinates[0]);
		double yDif = (coordinates[1]-pCoordinates[1]);
		double xyDif = (Math.sqrt(Math.pow(xDif, 2)+Math.pow(yDif, 2)));
		double zDif = (coordinates[2]-pCoordinates[2]);
		if(xDif == 0) xDif += 0.000001;
		if(xyDif == 0) xyDif += 0.000001;
		rotations[0] = Math.atan(yDif/xDif);
		rotations[1] = Math.atan(zDif/xyDif);
		
		double hRot = rotations[0];
		double vRot = rotations[1];
		if(v.leftRot>v.rightRot)		// view is split across the 180/-180 thing
		{
			if(hRot>v.rightRot||hRot<v.leftRot) return false;
		} else
		{
			if(!(hRot<v.rightRot||hRot>v.leftRot)) return false;
		}
		if(v.bottomRot>v.topRot)		// view is split across the 180/-180 thing
		{
			if(vRot>v.topRot||vRot<v.bottomRot) return false;
		} else
		{
			if(!(vRot<v.topRot||vRot>v.bottomRot)) return false;
		}
		return true;
	}
	/*
	 * object that stores rotations and distance of view
	 */
	public class ViewRotations {
		protected double topRot, bottomRot, leftRot, rightRot;
	    protected double distanceFromPanel;
	    protected double [] pRotation;
	}
	/**
	 * returns the rotations to each end of the screen
	 * @return the rotations for top right etc.
	 */
	protected ViewRotations getView()
	{
		double distanceFromPanel = zoom; //make this some function of zoom.
		double panelWidth = screenWidth*viewSize; // has to keep proportions with screen width and height
		double panelHeight = screenHeight*viewSize; // width and height are half full width/height
		
		double rotToPanelSide = Math.atan(panelWidth/distanceFromPanel);
		double rotToPanelTop = Math.atan(panelHeight/distanceFromPanel);
		double[] rotations = control.player.getRotation();
		ViewRotations view = new ViewRotations();
		view.pRotation = rotations;
		view.topRot = rotations[1]+rotToPanelTop;
		view.bottomRot = rotations[1]-rotToPanelTop;
		view.rightRot = rotations[0]+rotToPanelSide;
		view.leftRot = rotations[0]-rotToPanelSide;
		view.distanceFromPanel = distanceFromPanel;
		return view;
	}
	/*
	 * object that stores a point with vectors pointing to the two closest points on rect
	 */
	public class PointWithVector
	{
	    protected double[] location, previous, next;
	    /**
	     * constructor for first made four points
	     * @param panelHandle the whole panel for deletion and branching
	     * @param index index of this point in panel
	     * @param view view to draw panel into
	     */
	    public PointWithVector(PanelWithVectors panelHandle, int index, ViewRotations view)
	    {
	    	location = panelHandle.panel[index];
	    	if(index == 0)
	    	{
	    		previous = panelHandle.panel[3];
	    		next = panelHandle.panel[1];
	    	} else if(index == 3)
	    	{
	    		previous = panelHandle.panel[2];
	    		next = panelHandle.panel[0];
	    	} else
	    	{
	    		previous = panelHandle.panel[index-1];
	    		next = panelHandle.panel[index+1];
	    	}
			if(!panelHandle.graphics.pointOnScreen(location))
			{
				double [] p1 = getIntercept(location, previous, panelHandle);
				double [] p2 = getIntercept(location, next, panelHandle);
				if(p1 != null && p2 != null)				// if both ways are open
				{
					location = p1;
					panelHandle.branchPoint(index, p2);
				}
				if(p1 == null && p2 == null)				// if neither vector goes into view
				{
					panelHandle.deletePoint(index);
				}
				if(p1 != null && p2 == null)				// if only first vector works
				{
					location = p1;
				}
				if(p1 == null && p2 != null)				// if only second vector works
				{
					location = p2;
				}
			}
	    }
	    /**
	     * constructor for point made by branch
	     * @param point location of new point
	     */
	    public PointWithVector(double [] point)
	    {
	    	location = point;
	    }
	    /**
	     * get where line first comes into view, if it doesn't, return null
	     * @param p1 the location of the point
	     * @param p2 location of the next point
	     * @return where line first hits players view, starting at location
	     */
	    public double[] getIntercept(double [] start, double [] end, PanelWithVectors panel)
	    {
	    	double [] location = panel.graphics.control.player.getLocation();
	    	double [] rotations = panel.graphics.v.pRotation;
	    	
	    	double distanceFromPanel = zoom; //make this some function of zoom. //TODO unify
			double panelWidth = screenWidth*viewSize; // has to keep proportions with screen width and height
			double panelHeight = screenHeight*viewSize; // width and height are half full width/height
			double rotToPanelSide = Math.atan(panelWidth/distanceFromPanel);
			double rotToPanelTop = Math.atan(panelHeight/distanceFromPanel);
	    	
			double distanceSee = 100000;
			
			double rotRight = rotations[0]+rotToPanelSide;
			double rotLeft = rotations[0]-rotToPanelSide;
	    	double [] playerRight = {distanceSee*Math.cos(rotRight), distanceSee*Math.sin(rotRight)};	// x and y
	    	double [] playerLeft = {distanceSee*Math.cos(rotLeft), distanceSee*Math.sin(rotLeft)};	// x and y
	    	
	    	double [] startXY = {start[0]-location[0], start[1]-location[1]};
	    	double [] endXY = {end[0]-location[0], end[1]-location[1]};
			
			double rotTop = rotations[1]+rotToPanelTop;
			double rotBot = rotations[1]-rotToPanelTop;
	    	double [] playerTop = {distanceSee*Math.cos(rotTop), distanceSee*Math.sin(rotTop)};	// d and z
	    	double [] playerBot = {distanceSee*Math.cos(rotBot), distanceSee*Math.sin(rotBot)};// d and z
	    	
	    	double [] startDZ = {Math.sqrt(Math.pow(startXY[0], 2)+Math.pow(startXY[1], 2)), start[2]-location[2]};
	    	double [] endDZ = {Math.sqrt(Math.pow(endXY[0], 2)+Math.pow(endXY[1], 2)), end[2]-location[2]};
	    	
	    	double [] playerXY = {0, 0};
	    	double [] playerDZ = {0, 0};
	    	//player is at 0, 0 for both x: y, and d: z
	    	//rotRight: vector 100m away on right hand side of players sight
	    	//rotLeft: vector 100m away on left hand side of players sight
	    	//rotTop: vector 100m away on top of players sight
	    	//rotBot: vector 100m away on bottom of players sight
	    	//startXY: x, y of start
	    	//endXY: x, y of end
	    	//startDZ: d, z of start		(D: distance from player, Z: height difference)
	    	//endDZ: d, z of end
			
	    	//Now we have a bunch of lines between points
	    	//we need to check if they hit within their domains
	    	double hitRight2D = pointCollision(playerXY, playerRight, startXY, endXY);
	    	double hitLeft2D = pointCollision(playerXY, playerLeft, startXY, endXY);
	    	double hitTop2D = pointCollision(playerDZ, playerTop, startDZ, endDZ);
	    	double hitBot2D = pointCollision(playerDZ, playerBot, startDZ, endDZ);
			double distanceFromStart = getLowest(hitRight2D, hitLeft2D, hitTop2D, hitBot2D);
			
	    	if(distanceFromStart == 2) return null; // all intercepts returned 2 so nothing hit
	    	
	    	// distance from start is a decimal so do a weighted averge of start and finish
	    	double sW = 1-distanceFromStart;	//weighting of start and end
	    	double eW = distanceFromStart;
	    	double [] hit = {((sW*start[0])+(eW*end[0])),
	    					((sW*start[1])+(eW*end[1])),
	    					((sW*start[2])+(eW*end[2]))};
	    	return hit;
	    }
	    private double getLowest(double a, double b, double c, double d)
	    {
	    	if(a<b && a<c && a<d) return a;
	    	if(b<c && b<d) return b;
	    	if(c<d) return c;
	    	return d;
	    }
	    /**
	     * checks and returns where two lines intercept, as a function
	     * of distance from starting point, from 0-1, return 2 if they don't
	     * @param start1	start of first line
	     * @param end1		end of first line
	     * @param start2	start of second line
	     * @param end2		end of second line
	     * @return			where lines collide, null if they don't
	     */
	    protected double pointCollision(double [] start1, double [] end1, double [] start2, double [] end2)
	    {
	    	double M1 = (end1[1]-start1[1])/(end1[0]-start1[0]); // convert to y=mx+b form
	    	double M2 = (end2[1]-start2[1])/(end2[0]-start2[0]); // convert to y=mx+b form
	    		//	m1(x-x1)+y1 = m2(x-x2)+y2           x1:start1[0], y1:start1[1]
	    		//	x = (y2 - y1 + m1x1 - m2x2)/(m1 - m2)
	    	double x = (start2[1]-start1[1] + (M1*start1[0]) - (M2*start2[0]))/(M1 - M2);
	    		//	y = m1*(x-x1)+y1
	    	double y = M1*(x - start1[0]) + start1[1];
	    	double [] collision = {x, y};
	    	//	We have our intercept now is it within our lines
	    	if(start1[0]>end1[0])		// check first line
	    	{
	    		if(x<end1[0] || x>start1[0]) return 2;	// is it outside? return false
	    	} else
	    	{
	    		if(x>end1[0] || x<start1[0]) return 2;
	    	}
	    	if(start2[0]>end2[0])
	    	{
	    		if(x<end2[0] || x>start2[0]) return 2;
	    	} else
	    	{
	    		if(x>end2[0] || x<start2[0]) return 2;
	    	}
	    	// the distance as a decimal can be found by (colx-startx)/(endx-startx)
	    	
	    	return (collision[0]-start2[0])/(end2[0]-start2[0]);
	    }
	}
	/*
	 * object that stores 4 point with vectors pointing around the polygon
	 */
	public class PanelWithVectors
	{
		PointWithVector [] points = new PointWithVector[8];
		protected double[][] panel;
		protected Graphics graphics;
		/**
		 * makes points and lets them fix themselves to be on screen
		 * @param panelSet the information for the panel to create and fix
		 * @param view the view to fit panel into
		 * @param graphicsSet the main file, for use of rotationSetOnScreen
		 */
		public PanelWithVectors(double[][] panelSet, ViewRotations view, Graphics graphicsSet)
		{
			graphics = graphicsSet;
			panel = panelSet;
			for(int i = 0; i < 4; i++)
			{
				points[i*2] = new PointWithVector(this, i, view);
			}
		}
		protected void deletePoint(int index)
		{
			points[index] = null;
		}
		protected void branchPoint(int index, double[] p1)
		{
			points[index+1] = new PointWithVector(p1);
		}
	}
}