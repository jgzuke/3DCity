package com.ThreeDCity;

/*
 * Controlls movements nd rotation of view
 */
public final class Player
{
	protected int height = 190; //cm
	protected int x = 0;
	protected int y = 0;
	protected int z = 190; //height of eyes
	protected int hRotation = 0; //looking directly along x axis
	protected int zRotation = 0; //looking directly forward
	protected int tilt = 0; // tilting head to one side or other
	private Controller control;
	public Player(Controller controlSet)
	{
		control = controlSet;
	}
	protected double[] getLocation()
	{
		double[] location = {x, y, z};
		return location;
	}
	protected double[] getRotation()
	{
		double[] location = {x, y, z};
		return location;
	}
}