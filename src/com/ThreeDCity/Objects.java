package com.ThreeDCity;

import java.util.ArrayList;

import android.graphics.Color;

/*
 * hold descriptions of all objects in environment
 */
public final class Objects
{
	private Controller control;
	/*
	 * object that stores a panel, the five points and color
	 */
	public class Panel
	{
		int [][] points;
		int color;
		public Panel(int [][] pointsSet, int colorSet)
		{
			points = pointsSet;
			color = colorSet;
		}
	}
	public class Point
	{
		double [] pos; // x, y, z
		public Point(double [] posSet)
		{
			pos = posSet;
		}
		public Point(double x, double y, double z)
		{
			double [] array = {x, y, z};
			pos = array;
		}
	}
	protected ArrayList<Panel> panels = new ArrayList<Panel>();
	protected ArrayList<Point> points = new ArrayList<Point>();
	// each panes has five points, four corners [0-3] and a middle [4]
	// each point is {x, y, z}
	// everything is in cm for sizing.
	public Objects(Controller controlSet)
	{
		control = controlSet;
		makeBackGround();
	}
	protected void frameCall()
	{
		//objects dont do anything yet, just collection of data
	}
	private void makeBackGround()
	{
		makePoint(500, -140, 0);
		makePoint(500, -120, 0);
		makePoint(500, -100, 0);
		makePoint(500, -80, 0);
		makePoint(500, -60, 0);
		makePoint(500, -40, 0);
		makePoint(500, -20, 0);
		makePoint(500, 0, 0);
		makePoint(500, 20, 0);
		makePoint(500, 40, 0);
		makePoint(500, 60, 0);
		makePoint(500, 80, 0);
		makePoint(500, 100, 0);
		makePoint(500, 120, 0);
		makePoint(500, 140, 0);
		/*makeCube(500, 0, 50, 50);
		makeCube(900, 0, 50, 50);
		makeCube(700, 0, 50, 50);
		makeCube(500, 200, 50, 50);
		makeCube(900, 200, 50, 50);
		makeCube(700, 200, 50, 50);
		makeCube(500, -200, 50, 50);
		makeCube(900, -200, 50, 50);
		makeCube(700, -200, 50, 50);*/
	}
	private void makePoint(int x, int y, int z)
	{
		points.add(new Point(x, y, z));
	}
	private void makeCube(int x, int y, int z, int w)
	{
		makePanel(x+w, y+w, z-w, x+w, y-w, z-w, x-w, y-w, z-w, x-w, y+w, z-w, Color.rgb(200, 200, 200)); // bottom
		makePanel(x+w, y+w, z+w, x+w, y-w, z+w, x-w, y-w, z+w, x-w, y+w, z+w, Color.rgb(100, 100, 200)); // top
		
		makePanel(x+w, y+w, z-w, x+w, y-w, z-w, x+w, y-w, z+w, x+w, y+w, z+w, Color.rgb(240, 180, 200)); // left
		makePanel(x-w, y+w, z-w, x-w, y-w, z-w, x-w, y-w, z+w, x-w, y+w, z+w, Color.rgb(200, 200, 200)); // right
		
		makePanel(x+w, y+w, z-w, x-w, y+w, z-w, x-w, y+w, z+w, x+w, y+w, z+w, Color.rgb(80, 200, 200)); // back
		makePanel(x+w, y-w, z-w, x-w, y-w, z-w, x-w, y-w, z+w, x+w, y-w, z+w, Color.rgb(200, 20, 200)); // front
	}
	private void makePanel(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int x4, int y4, int z4, int color)
	{
		int[][] newPanel = {{x1, y1, z1}, {x2, y2, z2}, {x3, y3, z3}, {x4, y4, z4}, 
		{(x1+x2+x3+x4)/4, (y1+y2+y3+y4)/4, (z1+z2+z3+z4)/4}};
		Panel p = new Panel(newPanel, color);
		panels.add(p);
	}
}