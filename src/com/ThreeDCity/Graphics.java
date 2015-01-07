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
	/**
	 * 
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		double[][] viewPanel =  getViewPanel();
		
	}
	protected int[][] fixPanel(int[][] oldPanel)
	{
		int[][] panel = oldPanel.clone();
		
		return panel;
	}
	/**
	 * projects a point onto players viewPanel and returns x, y screen coordinates
	 * @param point		point to project
	 * @param viewPanel	view panel to project onto
	 * @return			x and y position of point on screen
	 */
	protected int [] projectOnViewPanel(double [] point, double [][] viewPanel)
	{
		int [] coordinates = new int[2];
		
		return coordinates;
	}
	/**
	 * returns a panel objects will project onto
	 * @return the virtual panel which your phone sees through
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
		double[] rotations = control.player.getRotation();
		
		double[] topLeft = control.player.getLocation().clone();
		topLeft[0]+= distanceToCorner*Math.cos(rotations[0]-rotToPanelSide)*Math.cos(rotations[1]+rotToPanelTop);
		topLeft[1]+= distanceToCorner*Math.sin(rotations[0]-rotToPanelSide)*Math.cos(rotations[1]+rotToPanelTop);
		topLeft[2]+= distanceToCorner*Math.sin(rotations[1]+rotToPanelTop);
		
		double[] topRight = control.player.getLocation().clone();
		topRight[0]+= distanceToCorner*Math.cos(rotations[0]+rotToPanelSide)*Math.cos(rotations[1]+rotToPanelTop);
		topRight[1]+= distanceToCorner*Math.sin(rotations[0]+rotToPanelSide)*Math.cos(rotations[1]+rotToPanelTop);
		topRight[2]+= distanceToCorner*Math.sin(rotations[1]+rotToPanelTop);
		
		double[] botLeft = control.player.getLocation().clone();
		botLeft[0]+= distanceToCorner*Math.cos(rotations[0]-rotToPanelSide)*Math.cos(rotations[1]-rotToPanelTop);
		botLeft[1]+= distanceToCorner*Math.sin(rotations[0]-rotToPanelSide)*Math.cos(rotations[1]-rotToPanelTop);
		botLeft[2]+= distanceToCorner*Math.sin(rotations[1]-rotToPanelTop);
		
		double[] botRight = control.player.getLocation().clone();
		botRight[0]+= distanceToCorner*Math.cos(rotations[0]+rotToPanelSide)*Math.cos(rotations[1]-rotToPanelTop);
		botRight[1]+= distanceToCorner*Math.sin(rotations[0]+rotToPanelSide)*Math.cos(rotations[1]-rotToPanelTop);
		botRight[2]+= distanceToCorner*Math.sin(rotations[1]-rotToPanelTop);
		
		// view pane is the square on which everything will 'project' going towards players x, y, z position
		double [][] viewPanel = {topLeft, topRight, botLeft, botRight}; // four corners, each with x, y, z
		return viewPanel;
	}
}