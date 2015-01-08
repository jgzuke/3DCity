package com.ThreeDCity;

import java.util.ArrayList;
import java.util.Arrays;

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
			int dist = 0;
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
		double[][] rotations = getRotationSet(panel, view);	// and returns rotations to points
														
		if(rotations != null)							// if panel was on screen
		{
			Path path = new Path();
			int numPoints = rotations.length;
			int[] point = projectOnView(rotations[numPoints-1], view);
			path.moveTo(point[0], point[1]);				// start at last corner
			for(int i = 0; i < numPoints; i++)
			{
				point = projectOnView(rotations[i], view);	// make lines to each corner
				path.lineTo(point[0], point[1]);
			}
			g.drawPath(path, paint);						// draws polygon
		}
	}
	/**
	 * fixes panel so it can be drawn in bounds
	 * @param oldPanel	panel sent to function
	 * @return			panel that can be drawn right
	 */
	protected double[][] getRotationSet(int[][] panel, ViewRotations view)
	{
		double [][] rotations = {	getRotationsToPoint(panel[0], view), 
								getRotationsToPoint(panel[1], view), 
								getRotationsToPoint(panel[2], view), 
								getRotationsToPoint(panel[3], view)};
		fixPanelRotationSet(rotations, view);
		return rotations;
	}
	/*
	 * 
	 */
	public class ViewRotations {
	    protected double topRot;
	    protected double bottomRot;
	    protected double leftRot;
	    protected double rightRot;
	    protected double distanceFromPanel;
	}
	/**
	 * fixes rotations of a panel to draw on screen, if panel not visible returns null
	 * @param rotations	the rotations to fix
	 * @param view		the view to fit rotations into
	 */
	protected void fixPanelRotationSet(double[][] rotations, ViewRotations view)
	{
		boolean anythingOnScreen = false;
		boolean [] onScreen = new boolean[4];
		Arrays.fill(onScreen, false);
		for(int i = 0; i < 4; i++)
		{
			if(rotationSetOnScreen(rotations[i], view))
			{
				anythingOnScreen = true;
				onScreen[i] = true;
			}
		}
		if(!anythingOnScreen)
		{
			rotations = null;
			return;
		}
				// IF AT LEAST PART OF SQUARE ON SCREEN
		//TODO
		//if()
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
	    protected double topRot;
	    protected double bottomRot;
	    protected double leftRot;
	    protected double rightRot;
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