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
	// all panels must have a center as their last point
	protected ArrayList<Panel> panels = new ArrayList<Panel>();
	// each point is {x, y, z}
	// everything is in cm for sizing.
	public Objects(Controller controlSet)
	{
		control = controlSet;
		makeBackGround();
	}
	private void makeBackGround()
	{
		int a = 10000;
		makeJZ();
		makeCube(825, 0, 50, 50, Color.rgb(200, 200, 200));
		makeCube(625, 0, 50, 50, Color.rgb(200, 200, 100));
		makeCube(425, 200, 50, 50, Color.rgb(200, 100, 200));
		makeCube(825, 200, 50, 50, Color.rgb(200, 100, 100));
		makeCube(625, 200, 50, 50, Color.rgb(100, 200, 200));
		makeCube(425, -200, 50, 50, Color.rgb(100, 200, 100));
		makeCube(825, -200, 50, 50, Color.rgb(100, 100, 200));
		makeCube(625, -200, 50, 50, Color.rgb(100, 100, 100));
	}
	private void makeJZ()
	{
		int [][] jFront = { 
				{500, 216, 169}, 
				{500, 216, 119}, 
				{500, 163, 68}, 
				{500, 99, 68}, 
				{500, 41, 121}, 
				{500, 41, 342}, 
				{500, 96, 342}, 
				{500, 96, 148}, 
				{500, 130, 114}, 
				{500, 164, 146}, 
				{500, 164, 169}, 
				{500, 129, 205}
		};
		int [][] jBack = { 
				{550, 216, 169}, 
				{550, 216, 119}, 
				{550, 163, 68}, 
				{550, 99, 68}, 
				{550, 41, 121}, 
				{550, 41, 342}, 
				{550, 96, 342}, 
				{550, 96, 148}, 
				{550, 130, 114}, 
				{550, 164, 146}, 
				{550, 164, 169}, 
				{550, 129, 205}
		};
		int [][][] jS1 = {	{{500, 216, 169},	{500, 216, 119}, 	{550, 216, 119}, 	{550, 216, 169}},
							{{500, 216, 119},	{500, 163, 68}, 	{550, 163, 68}, 	{550, 216, 119}},
							{{500, 163, 68},	{500, 99, 68}, 		{550, 99, 68}, 		{550, 163, 68}},
							{{500, 99, 68}, 	{500, 41, 121}, 	{550, 41, 121}, 	{550, 99, 68}},
							{{500, 41, 121}, 	{500, 41, 342}, 	{550, 41, 342}, 	{550, 41, 121}},
							{{500, 41, 342}, 	{500, 96, 342}, 	{550, 96, 342}, 	{550, 41, 342}},
							{{500, 96, 342}, 	{500, 96, 148}, 	{550, 96, 148}, 	{550, 96, 342}},
							{{500, 96, 148}, 	{500, 130, 114}, 	{550, 130, 114}, 	{550, 96, 148}},
							{{500, 130, 114}, 	{500, 164, 146}, 	{550, 164, 146}, 	{550, 130, 114}},
							{{500, 164, 146}, 	{500, 164, 169}, 	{550, 164, 169}, 	{550, 164, 146}},
							{{500, 164, 169}, 	{500, 129, 205}, 	{550, 129, 205}, 	{550, 164, 169}},
							{{500, 129, 205}, 	{500, 216, 169}, 	{550, 216, 169}, 	{550, 129, 205}}};
		
		int [][] zFront = { 
				{500, 6, 69}, 
				{500, -216, 69}, 
				{500, -216, 115}, 
				{500, -60, 115}, 
				{500, -210, 299}, 
				{500, -210, 342}, 
				{500, -10, 342}, 
				{500, -10, 296}, 
				{500, -137, 296}, 
				{500, 6, 119}, 
				{500, -105, 205}
		};
		int [][] zBack = { 
				{550, 6, 69}, 
				{550, -216, 69}, 
				{550, -216, 115}, 
				{550, -60, 115}, 
				{550, -210, 299}, 
				{550, -210, 342}, 
				{550, -10, 342}, 
				{550, -10, 296}, 
				{550, -137, 296}, 
				{550, 6, 119}, 
				{550, -105, 205}
		};
		int [][][] zS1 = {
				{{500, 6, 69}, 		{500, -216, 69}, 	{550, -216, 69}, 	{550, 6, 69}},
				{{500, -216, 69}, 	{500, -216, 115}, 	{550, -216, 115}, 	{550, -216, 69}},
				{{500, -216, 115}, 	{500, -60, 115}, 	{550, -60, 115}, 	{550, -216, 115}},
				{{500, -60, 115}, 	{500, -210, 299}, 	{550, -210, 299}, 	{550, -60, 115}},
				{{500, -210, 299}, 	{500, -210, 342}, 	{550, -210, 342}, 	{550, -210, 299}},
				{{500, -210, 342}, 	{500, -10, 342}, 	{550, -10, 342}, 	{550, -210, 342}},
				{{500, -10, 342}, 	{500, -10, 296}, 	{550, -10, 296}, 	{550, -10, 342}},
				{{500, -10, 296}, 	{500, -137, 296}, 	{550, -137, 296}, 	{550, -10, 296}},
				{{500, -137, 296}, 	{500, 6, 119}, 		{550, 6, 119}, 		{550, -137, 296}},
				{{500, 6, 119}, 	{500, -105, 205}, 	{550, -105, 205}, 	{550, 6, 119}},
				{{500, -105, 205}, 	{500, 6, 69}, 		{550, 6, 69}, 		{550, -105, 205}}
		};
		makePanel(jFront, Color.rgb(200, 200, 200));
		makePanel(zFront, Color.rgb(200, 200, 200));
		makePanel(jBack, Color.rgb(200, 200, 200));
		makePanel(zBack, Color.rgb(200, 200, 200));
		for(int i = 0; i < 12; i++)
		{
			makePanel(jS1[i], Color.rgb(200, 200, 200));
		}
		for(int i = 0; i < 11; i++)
		{
			makePanel(zS1[i], Color.rgb(200, 200, 200));
		}
	}
	private void makeCube(int x, int y, int z, int w, int color)
	{
		makePanel(x+w, y+w, z-w, x+w, y-w, z-w, x-w, y-w, z-w, x-w, y+w, z-w, color); // bottom
		makePanel(x+w, y+w, z+w, x+w, y-w, z+w, x-w, y-w, z+w, x-w, y+w, z+w, color); // top
		
		makePanel(x+w, y+w, z-w, x+w, y-w, z-w, x+w, y-w, z+w, x+w, y+w, z+w, color); // left
		makePanel(x-w, y+w, z-w, x-w, y-w, z-w, x-w, y-w, z+w, x-w, y+w, z+w, color); // right
		
		makePanel(x+w, y+w, z-w, x-w, y+w, z-w, x-w, y+w, z+w, x+w, y+w, z+w, color); // back
		makePanel(x+w, y-w, z-w, x-w, y-w, z-w, x-w, y-w, z+w, x+w, y-w, z+w, color); // front
	}
	private void makePanel(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int x4, int y4, int z4, int color)
	{
		int[][] newPanel = {{x1, y1, z1}, {x2, y2, z2}, {x3, y3, z3}, {x4, y4, z4}, 
		{(x1+x2+x3+x4)/4, (y1+y2+y3+y4)/4, (z1+z2+z3+z4)/4}};
		makePanel(newPanel, color);
	}
	private void makePanel(int [] x, int [] y, int [] z, int color)
	{
		int[][] newPanel = new int[x.length+1][3];
		int xSum = 0;
		int ySum = 0;
		int zSum = 0;
		for(int i = 0; i < x.length; i++)
		{
			newPanel[i][0] = x[i];
			newPanel[i][1] = y[i];
			newPanel[i][2] = z[i];
			xSum += x[i];
			ySum += y[i];
			zSum += z[i];
		}
		newPanel[x.length][0] = xSum/x.length;
		newPanel[x.length][1] = ySum/x.length;
		newPanel[x.length][2] = zSum/x.length;
		makePanel(newPanel, color);
	}
	private void makePanel(int [][] points, int color)
	{
		Panel p = new Panel(points, color);
		panels.add(p);
	}
}