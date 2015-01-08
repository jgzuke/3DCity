package com.ThreeDCity;

import java.util.ArrayList;

/*
 * hold descriptions of all objects in environment
 */
public final class Objects
{
	private Controller control;
	protected ArrayList<int[][]> panels = new ArrayList<int[][]>();
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
		makePanel(400, -200, 400, 400, 200, 400, 400, 200, 100, 400, -200, 100);
		makePanel(400, -200, 400, 800, -200, 400, 800, -200, 100, 400, -200, 100);
	}
	private void makePanel(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int x4, int y4, int z4)
	{
		int[][] newPanel = {{x1, y1, z1}, {x2, y2, z2}, {x3, y3, z3}, {x4, y4, z4}, 
		{(x1+x2+x3+x4)/4, (y1+y2+y3+y4)/4, (z1+z2+z3+z4)/4}};
		panels.add(newPanel);
	}
}