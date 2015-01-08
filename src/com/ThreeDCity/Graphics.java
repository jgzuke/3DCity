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
	 * object containing all data about a panel neccesary to fix points onto screen
	 */
	public class PanelWithDirection
	{
	    protected double topRot;
	    protected double bottomRot;
	    protected double leftRot;
	    protected double rightRot;
	    protected double distanceFromPanel;
	    /**
	     * constructor takes panel points and labels each point with direction vectors
	     * this lets you find out where a single point sticks when forced on screen
	     */
	    public PanelWithDirection(double[][] rotations)
	    {
	    	
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
	    	} else
	    	{
	    		previous = panelHandle.panel[index-1];
	    	}
	    	if(index == 3)
	    	{
	    		next = panelHandle.panel[0];
	    	} else
	    	{
	    		next = panelHandle.panel[index-1];
	    	}
			if(!panelHandle.graphics.rotationSetOnScreen(getRotationsToPoint(location, view), view))
			{
				int [] p1 = getIntercept(location, previous);
				int [] p2 = getIntercept(location, next);
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
	    public int[] getIntercept(int [] p1, int [] p2)
	    {
	    	//TODO
	    	return p1;
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
			for(int i = 0; i < 4; i+=2)
			{
				points[i] = new PointWithVector(this, i, view);
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
	 * returns rotations 	to a point
	 * @param coordinates 	coordinates of point
	 * @param view			view being projected onto
	 * @return				rotations to point
	 */
	protected double[] getRotationsToPoint(int [] coordinates, ViewRotations view)
	{
		double [] pCoordinates = control.player.getLocation();
		double [] rotations = new double[2];
		int xDif = (int)(coordinates[0]-pCoordinates[0]);
		int yDif = (int)(coordinates[1]-pCoordinates[1]);
		int xyDif = (int)(Math.sqrt(Math.pow(xDif, 2)+Math.pow(yDif, 2)));
		int zDif = (int)(coordinates[2]-pCoordinates[2]);
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