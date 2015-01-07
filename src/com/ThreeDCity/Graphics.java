package com.ThreeDCity;

import android.content.Context;
import android.graphics.Canvas;
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
	@Override
	protected void onDraw(Canvas g)
	{
		
	}
	/*
	 * returns a panel objects will project onto
	 */
	protected double[][] getViewPanel()
	{
		double distanceFromPanel = 5; //make this some function of zoom.
		double viewSize = 1/200;
		double panelWidth = screenWidth*viewSize; // has to keep proportions with screen width and height
		double panelHeight = screenHeight*viewSize; // width and height are half full width/height
		double rotToPanelSide = Math.atan(panelWidth/distanceFromPanel);
		double rotToPanelTop = Math.atan(panelHeight/distanceFromPanel);
		double distanceToCorner = Math.sqrt(Math.pow(distanceFromPanel, 2)+ // get distance to each corner
											Math.pow(panelWidth, 2)+
											Math.pow(panelHeight, 2));
		// find four corners for a view to project on
		double
		double[] topLeft = control.player.getLocation().clone();
		topLeft.
		double[] topRight = control.player.getLocation().clone();
		double[] botLeft = control.player.getLocation().clone();
		double[] botRight = control.player.getLocation().clone();
		// view pane is the square on which everything will 'project' going towards players x, y, z position
		double [][] viewPanel = {topLeft, topRight, botLeft, botRight}; // four corners, each with x, y, z
		return viewPanel;
	}
}