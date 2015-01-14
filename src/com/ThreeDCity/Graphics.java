package com.ThreeDCity;

import java.util.ArrayList;
import java.util.List;

import com.ThreeDCity.Objects.Panel;
import android.content.Context;
import android.graphics.Bitmap;
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
	double screenWidth, screenHeight;
	
	
	private double x, y, z;
	private double hRot, zRot, tRot;
	// factor to multiply points by to get edge of player view drawn at edge o screen
	// at rotToTop etc you should draw edge of screen
	
    
	private Controller control;
	private Paint paint;
	private int screenSize = 4000;
	private int halfScreenSize = screenSize/2;
	private double zoom = 1;
	
	public Graphics(Context contextSet, Controller controlSet, double [] dimensions)
	{
		super(contextSet);
		screenWidth = dimensions[0];
		screenHeight = dimensions[1];
		control = controlSet;
		paint = new Paint();
	}
	/**
	 * draws every object as well as the ground in the distance
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		getView();
		//	(screenHeight/2) = Math.tan(rotToPanelTop)*distanceFromPanel*testing
		//	testing = (screenHeight)/(2*Math.tan(rotToPanelTop)*distanceFromPanel)
		g.translate((int)(screenWidth/2), (int)(screenHeight/2));
		
		Path path = new Path();
		path.moveTo(-5000, 0);//(float)-Math.sin(tRot)*5000);	// top left
		path.lineTo(5000, 0);//(float)Math.sin(tRot)*5000);	// top right
		path.lineTo(5000, 5000);	// bot right
		path.lineTo(5000, -5000);	// bot left
		path.lineTo(-5000, 0);//(float)-Math.sin(tRot)*5000);	// top left
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GREEN);
		g.drawPath(path, paint);
		
		ArrayList<Panel> panels = (ArrayList<Panel>) control.objects.panels;
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
		List<Integer> orderToDraw = new ArrayList<>();
		int [] distances = new int[panels.size()];
		for(int i = 0; i < panels.size(); i++)
		{
			int center = panels.get(i).points.length-1;
			int [] point = panels.get(i).points[center];
			distances[i] = ((int) Math.sqrt(	Math.pow(point[0]-x, 2)+	// X-Direction
											Math.pow(point[1]-y, 2)+	// Y-Direction
											Math.pow(point[2]-z, 2)));	// Z-Direction
		}
		for(int i = 0; i < panels.size(); i++)	// first elements should be farthest, so drawn overtop
		{
			int index = 0;
			int dist = 0;
			for(int j = 0; j < panels.size(); j++)	// check every elements left
			{
				while(true)
				{
					boolean skipped = false;
					for(int k = 0; k < i; k++)
					{
						if(orderToDraw.get(k)==j)
						{
							j++;			// if this index is in sorted list skip it
							skipped = true;
						}
					}
					if(!skipped)
					{
						break;
					}
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
			orderToDraw.add(index);
		}
		int [] intArray = new int [panels.size()];
		for(int i = 0; i < panels.size(); i ++)
		{
			intArray[i]=orderToDraw.get(i);
		}
		return intArray;
	}
	/**
	 * draws panel sent to function
	 * @param panel the panel to draw
	 */
	protected void drawPanel(Panel panel, Canvas g)
	{
		ArrayList<double[]> p = getScreenPointSet(panel.points);	// and returns rotations to points
		if(p.size() > 0)							// if panel was on screen
		{
			Path path = new Path();
			int num = p.size();
			path.moveTo((int)p.get(num-1)[0], (int)p.get(num-1)[1]);// start at last corner
			for(int i = 0; i < num; i++)
			{
				path.lineTo((int)p.get(i)[0], (int)p.get(i)[1]); // make lines to each corner
			}
			paint.setStyle(Style.FILL);
			paint.setColor(panel.color);
			g.drawPath(path, paint);
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLACK);
			g.drawPath(path, paint);
		}
	}
	/**
	 * returns coordintes of points on panel, if panel not visible returns null
	 * @param rotations	the rotations to fix
	 * @param view		the view to fit rotations into
	 */
	protected ArrayList<double[]> getScreenPointSet(int[][] panelSet)
	{
		double [][] panelSetDouble = new double[panelSet.length][3];
		for(int i = 0 ; i < panelSet.length; i++)
		{
			panelSetDouble[i] = relativeCoordinates(intToDoubleArray(panelSet[i]));
		}
		PanelWithVectors panel = new PanelWithVectors(panelSetDouble, this);
		ArrayList<double[]> p = new ArrayList<double[]>();
		for(int i = 0; i < (panelSet.length-1)*3; i++)
		{
			if(panel.points[i] != null)
			{
				p.add(getScreenPoint(panel.points[i].location));
			}
		}
		return p;
	}
	protected double [] intToDoubleArray(int [] start)
	{
		double [] end = new double[start.length];
		for(int i = 0; i < start.length; i++)
		{
			end[i] = start[i];
		}
		return end;
	}
	/**
	 * 
	 * @param pos
	 */
	protected double [] relativeCoordinates(double [] pos)
	{
		double xo = pos[0]-x;
		double yo = pos[1]-y;
		double zo = pos[2]-z;
		// rotate around origin by control.player rotations
		// rotate horizontally
		double x1 = (Math.cos(hRot)*xo) - (Math.sin(hRot)*yo);
		double y = (Math.sin(hRot)*xo) + (Math.cos(hRot)*yo);
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
	protected double [] getScreenPoint(double [] coordinates)
	{
		double [] ratios = {coordinates[1]/coordinates[0], coordinates[2]/coordinates[0]};
		double [] point = {halfScreenSize*ratios[0], 
								-halfScreenSize*ratios[1]};
		double [] pointTilted = {	(Math.cos(tRot)*point[0]) - (Math.sin(tRot)*point[1]),
									(Math.sin(tRot)*point[0]) + (Math.cos(tRot)*point[1])};
		return pointTilted;
	}
	/**
	 * returns the rotations to each end of the screen
	 * @return the rotations for top right etc.
	 */
	protected void getView()
	{
		x = control.player.x;
		y = control.player.y;
		z = control.player.z;
		hRot = control.player.hRotation;
		zRot = control.player.zRotation;
		tRot = control.player.tiltRotation;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * object that stores n point with vectors pointing around the polygon
	 */
	public class PanelWithVectors
	{
		PointWithVector [] points;
		protected double[][] panel;
		protected Graphics graphics;
		/**
		 * makes points and lets them fix themselves to be on screen
		 * @param panelSet the information for the panel to create and fix
		 * @param view the view to fit panel into
		 * @param graphicsSet the main file, for use of rotationSetOnScreen
		 */
		public PanelWithVectors(double[][] panelSet, Graphics graphicsSet)
		{
			points = new PointWithVector[(panelSet.length-1)*3];
			graphics = graphicsSet;
			panel = panelSet;
			for(int i = 0; i < panelSet.length-1; i++)
			{
				points[i*3] = new PointWithVector(this, i);
			}
		}
		protected void branchPoint(int index, double[] p1)
		{
			points[index+1] = new PointWithVector(p1);
		}
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
	    public PointWithVector(PanelWithVectors panelHandle, int index)
	    {
	    	location = panelHandle.panel[index];
	    	if(index == 0)
	    	{
	    		previous = panelHandle.panel[panelHandle.panel.length-2];
	    		next = panelHandle.panel[index+1];
	    	} else if(index == panelHandle.panel.length-2)
	    	{
	    		previous = panelHandle.panel[index-1];
	    		next = panelHandle.panel[0];
	    	} else
	    	{
	    		previous = panelHandle.panel[index-1];
	    		next = panelHandle.panel[index+1];
	    	}
	    	index *= 3;
			if(!pointOnScreen(location))
			{
				//TODO
				double [] p1 = getIntercept(location, previous);
				double [] p2 = getIntercept(location, next);
				//double [] p3 = getCorner(location, next, previous);
				if(p1 != null && p2 != null)				// if both ways are open
				{
					//location = p1;
					//panelHandle.branchPoint(index, p3);
					//panelHandle.branchPoint(index+1, p2);
				}
				if(p1 == null && p2 == null)				// if neither vector goes into view
				{
					//if(p3 != null) location = p3;
				}
				if(p1 != null && p2 == null)				// if only first vector works
				{
					//location = p1;
				}
				if(p1 == null && p2 != null)				// if only second vector works
				{
					//location = p2;
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
	     * get where line hits panel for corner lines. 
	     * @param start starting point to test
	     * @param end ending point
	     * @param panel
	     * @return
	     */
	    private double[] getCorner(double [] point, double [] next, double [] prev)
	    {
	    	double [] corner = {10000, point[1]/Math.abs(point[1])*20000, point[2]/Math.abs(point[2])*20000};	// x and y
	    	double [] v1 = {point[0]-next[0], point[1]-next[1], point[2]-next[2]};
	    	double [] v2 = {point[0]-prev[0], point[1]-prev[1], point[2]-prev[2]};
	    	double [] norm = {(v1[1]*v2[2])-(v1[2]*v2[1]), (v1[2]*v2[0])-(v1[0]*v2[2]), (v1[0]*v2[1])-(v1[1]*v2[0])};
	    	double k = (v1[0]*norm[0]) + (v1[1]*norm[1]) + (v1[2]*norm[2]);
	    	double b = (corner[0]*norm[0]) + (v1[1]*corner[1]) + (v1[2]*corner[2]);
	    	double ratio = k/b;
	    	double [] intercept = {corner[0]*ratio, corner[1]*ratio, corner[2]*ratio};
	    	return intercept;
	    }
	    /**
	     * get where line first comes into view, if it doesn't, return null
	     * @param p1 the location of the point
	     * @param p2 location of the next point
	     * @return where line first hits players view, starting at location
	     */
	    public double[] getIntercept(double [] start, double [] end)
	    {
			double [] startXY = {start[0], start[1]};
	    	double [] endXY = {end[0], end[1]};
	    	double [] startXZ = {start[0], start[2]};
	    	double [] endXZ = {end[0], end[2]};
			
	    	double [] playerRight = {10000, 20000};	// x and y
	    	double [] playerLeft = {10000, -20000};	// x and y
	    	double [] playerTop = {10000, 20000};		// d and z
	    	double [] playerBot = {10000, -20000};		// d and z
	    	
	    	double [] origin = {0, 0};
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
	    	
	    	double [] hitSides = new double[4]; // right, left, top, bot
	    	hitSides[0] = pointCollision(origin, playerRight, startXY, endXY);
	    	hitSides[1] = pointCollision(origin, playerLeft, startXY, endXY);
	    	hitSides[2] = pointCollision(origin, playerTop, startXZ, endXZ);
	    	hitSides[3] = pointCollision(origin, playerBot, startXZ, endXZ);
			
			for(int i = 0; i < 4; i ++)
			{
				double sW = 1-hitSides[i];	//weighting of start and end
		    	double eW = hitSides[i];
		    	double [] hit = {((sW*start[0])+(eW*end[0])),
    					((sW*start[1])+(eW*end[1])),
    					((sW*start[2])+(eW*end[2]))};
				if(!pointOnScreen(hit))
				{
					hitSides[i]=2;
				}
			}
				
				
			double distanceFromStart = getLowest(hitSides[0], hitSides[1], hitSides[2], hitSides[3]);
				
				
				//TODO fix your website dumy
				//TODO pick nicer font
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
		 * returns whether the rotation set is in players view
		 * @param rotationSet	the rotation set to check
		 * @param view			the view to check if it fits in
		 * @return				whether it fits in players view
		 */
		protected boolean pointOnScreen(double[] point)
		{
			// TODO check whether point is actually on screen
			if(point[0] <= 0.001) return false;
			double ratioXY = Math.abs(point[1]/point[0]);
			double ratioXZ = Math.abs(point[2]/point[0]);
			if(ratioXY>2) return false;
			if(ratioXZ>2) return false;
			return true;
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
}