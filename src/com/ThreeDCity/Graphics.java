package com.ThreeDCity;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/*
 * Draws all objects
 */
public final class Graphics extends View
{
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	protected double zoom;
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
	 * draws every object as wel as the ground in the distance
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		ViewRotations view =  getView();
		g.drawRect(0, (int)(Math.tan(control.player.zRotation)*view.distanceFromPanel), (int)screenWidth, (int)screenHeight, paint);
		ArrayList<int[][]> panels = control.objects.panels;
		int [] orderToDraw = orderPanels(panels);
		for(int i = 0; i < panels.size(); i++)
		{
			drawPanel(panels.get(orderToDraw[i]), view, g);
		}
	}
	/**
	 * layers panels to be drawn on top of each other
	 * @return which index will be drawn first, second etc.
	 */
	protected int[] orderPanels(ArrayList<int[][]> panels)
	{
		int [] orderToDraw = new int[panels.size()];
		int [] distances = new int[panels.size()];
		double [] playerPos = control.player.getLocation();
		for(int i = 0; i < panels.size(); i++)
		{
			distances[i] = (int) Math.sqrt(	Math.pow(panels.get(i)[4][0]-playerPos[0], 2)+	// X-Direction
											Math.pow(panels.get(i)[4][1]-playerPos[1], 2)+	// Y-Direction
											Math.pow(panels.get(i)[4][2]-playerPos[2], 2));	// Z-Direction
		}
		for(int i = 0; i < panels.size(); i++)	// first elements should be farthest, so drawn overtop
		{
			int index = 0;
			for(int j = 0; j < panels.size(); j++)	// check every elements left
			{
				for(int k = 0; k < i; k++)
				{
					if(orderToDraw[k]==j) j++;			// if this index is in sorted list skip it
				}
				if(j < panels.size())					// make sure we didn't just skip the last index
				{
					if(distances[j]>distances[index])		// if this object farther away
					{
						index = j;
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
	protected void drawPanel(int[][] rawPanel, ViewRotations view, Canvas g)
	{
		int[][] panel = rawPanel.clone();				// fixes panel so it fits on screen
		ArrayList<double[]> rotations = getRotationSet(panel, view);	// and returns rotations to points
		if(rotations.size() > 0)							// if panel was on screen
		{
			Path path = new Path();
			int numPoints = rotations.size();
			int[] point = projectOnView(rotations.get(numPoints-1), view);
			path.moveTo(point[0], point[1]);				// start at last corner
			for(int i = 0; i < numPoints; i++)
			{
				point = projectOnView(rotations.get(i), view);	// make lines to each corner
				path.lineTo(point[0], point[1]);
			}
			g.drawPath(path, paint);						// draws polygon
		}
	}
	/*
	 * object that stores a point with vectors pointing to the two closest points on rect
	 */
	public class PointWithVector
	{
	    protected int[] location, previous, next;
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
			if(!panelHandle.graphics.rotationSetOnScreen(getRotationsToPoint(location, view), view))
			{
				int [] p1 = getIntercept(location, previous, panelHandle);
				int [] p2 = getIntercept(location, next, panelHandle);
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
	    public PointWithVector(int [] point)
	    {
	    	location = point;
	    }
	    /**
	     * get where line first comes into view, if it doesn't, return null
	     * @param p1 the location of the point
	     * @param p2 location of the next point
	     * @return where line first hits players view, starting at location
	     */
	    public int[] getIntercept(int [] start, int [] end, PanelWithVectors panel)
	    {
	    	double [] location = panel.graphics.control.player.getLocation();
	    	double [] rotations = panel.graphics.control.player.getRotation();
	    	
	    	double distanceFromPanel = 5; //make this some function of zoom. //TODO unify
			double viewSize = 1/200;
			double panelWidth = screenWidth*viewSize; // has to keep proportions with screen width and height
			double panelHeight = screenHeight*viewSize; // width and height are half full width/height
			double rotToPanelSide = Math.atan(panelWidth/distanceFromPanel);
			double rotToPanelTop = Math.atan(panelHeight/distanceFromPanel);
	    	
			double distanceSee = 1000;
			
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
	    	int [] hit = {	(int)((sW*start[0])+(eW*end[0])),
	    					(int)((sW*start[1])+(eW*end[1])),
	    					(int)((sW*start[2])+(eW*end[2]))};
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
		protected int[][] panel;
		protected Graphics graphics;
		/**
		 * makes points and lets them fix themselves to be on screen
		 * @param panelSet the information for the panel to create and fix
		 * @param view the view to fit panel into
		 * @param graphicsSet the main file, for use of rotationSetOnScreen
		 */
		public PanelWithVectors(int[][] panelSet, ViewRotations view, Graphics graphicsSet)
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
		protected void branchPoint(int index, int[] p1)
		{
			points[index+1] = new PointWithVector(p1);
		}
	}
	/**
	 * fixes rotations of a panel to draw on screen, if panel not visible returns null
	 * @param rotations	the rotations to fix
	 * @param view		the view to fit rotations into
	 */
	protected ArrayList<double[]> getRotationSet(int[][] panelSet, ViewRotations view)
	{
		PanelWithVectors panel = new PanelWithVectors(panelSet.clone(), view, this);
		ArrayList<double[]> rotations = new ArrayList<double[]>();	// and returns rotations to points
		for(int i = 0; i < 8; i++)
		{
			if(panel.points[i] != null)
			{
				rotations.add(getRotationsToPoint(panel.points[i].location, view));
			}
		}
		return rotations;
	}
	/**
	 * returns whether the rotation set is in players view
	 * @param rotationSet	the rotation set to check
	 * @param view			the view to check if it fits in
	 * @return				whether it fits in players view
	 */
	protected boolean rotationSetOnScreen(double[] rotationSet, ViewRotations view)
	{
		double hRot = rotationSet[0];
		double vRot = rotationSet[1];
		if(view.leftRot>view.rightRot)		// view is split across the 180/-180 thing
		{
			if(hRot>view.rightRot||hRot<view.leftRot) return false;
		} else
		{
			if(!(hRot<view.rightRot||hRot>view.leftRot)) return false;
		}
		if(view.bottomRot>view.topRot)		// view is split across the 180/-180 thing
		{
			if(vRot>view.topRot||vRot<view.bottomRot) return false;
		} else
		{
			if(!(vRot<view.topRot||vRot>view.bottomRot)) return false;
		}
		return true;
	}
	/**
	 * returns rotations to a point
	 * @param coordinates 	coordinates of point
	 * @param view			view being projected onto
	 * @return				rotations to point
	 */
	protected double[] getRotationsToPoint(int [] coordinates, ViewRotations view)
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
		return rotations;
	}
	/**
	 * projects a set of rotations onto players view and returns x, y screen coordinates
	 * @param point		rotations to project
	 * @param viewPanel	view panel to project onto
	 * @return			x and y position of point on screen
	 */
	protected int [] projectOnView(double [] rotations, ViewRotations view)
	{
		int [] position = new int[2];
		position[0] = (int)(Math.tan(rotations[0])*view.distanceFromPanel);
		position[1] = (int)(Math.tan(rotations[1])*view.distanceFromPanel);
		return position;
	}
	/*
	 * object that stores rotations and distance of view
	 */
	public class ViewRotations {
	    protected double topRot, bottomRot, leftRot, rightRot;
	    protected double distanceFromPanel;
	}
	/**
	 * returns the rotations to each end of the screen
	 * @return the rotations for top right etc.
	 */
	protected ViewRotations getView()
	{
		double distanceFromPanel = 5; //make this some function of zoom.
		double viewSize = 1/200;
		double panelWidth = screenWidth*viewSize; // has to keep proportions with screen width and height
		double panelHeight = screenHeight*viewSize; // width and height are half full width/height
		double rotToPanelSide = Math.atan(panelWidth/distanceFromPanel);
		double rotToPanelTop = Math.atan(panelHeight/distanceFromPanel);
		double[] rotations = control.player.getRotation();
		ViewRotations view = new ViewRotations();
		view.topRot = rotations[1]+rotToPanelTop;
		view.bottomRot = rotations[1]-rotToPanelTop;
		view.rightRot = rotations[0]+rotToPanelSide;
		view.leftRot = rotations[0]-rotToPanelSide;
		view.distanceFromPanel = distanceFromPanel;
		return view;
	}
}