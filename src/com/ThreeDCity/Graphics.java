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
	 * 
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		ViewRotations view =  getView();
		ArrayList<int[][]> panels = control.objects.panels;
		int [] orderToDraw = orderPanels(panels);
		for(int i = 0; i < panels.size(); i++)
		{
			drawPanel(panels.get(orderToDraw[i]), view, g);
		}
		g.drawRect(0, (int)(Math.tan(control.player.zRotation)*view.distanceFromPanel), (int)screenWidth, (int)screenHeight, paint);
	}
	/**
	 * layers panels to be drawn on top of each other
	 * @return which index will be drawn first, second etc.
	 */
	protected int[] orderPanels(ArrayList<int[][]> panels)
	{
		int [] orderToDraw = new int[panels.size()];
		//TODO
		return orderToDraw;
	}
	/**
	 * draws panel sent to function
	 * @param panel the panel to draw
	 */
	protected void drawPanel(int[][] rawPanel, ViewRotations view, Canvas g)
	{
		int[][] panel = rawPanel.clone();
		fixPanel(panel);								// fixes panel so it fits on screen
		
		Path path = new Path();
		int[] point = projectOnView(rawPanel[3], view);
		path.moveTo(point[0], point[1]);				// start at last corner
		for(int i = 0; i < 4; i++)
		{
			point = projectOnView(rawPanel[i], view);	// make lines to each corner
			path.lineTo(point[0], point[1]);
		}
		
		g.drawPath(path, paint);						// draws polygon
	}
	/**
	 * fixes panel so it can be drawn in bounds
	 * @param oldPanel	panel sent to function
	 * @return			panel that can be drawn right
	 */
	protected void fixPanel(int[][] oldPanel)
	{
		//TODO
	}
	/**
	 * projects a point onto players view and returns x, y screen coordinates
	 * @param point		point to project
	 * @param viewPanel	view panel to project onto
	 * @return			x and y position of point on screen
	 */
	protected int [] projectOnView(int [] point, ViewRotations view)
	{
		int [] coordinates = new int[2];
		//TODO
		return coordinates;
	}
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