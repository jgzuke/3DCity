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
	private Controller control;
	public Player(Controller controlSet)
	{
		control = controlSet;
	}
	/**
	 * returns players x, y, z
	 * @return x, y, z coordinates
	 */
	protected double[] getLocation()
	{
		double[] location = {x, y, z};
		return location;
	}
	/**
	 *  returns players horizontal and vertical rotation
	 * @return horizontal and vertical rotation
	 */
	protected double[] getRotation()
	{
		double[] rotation = {hRotation, zRotation};
		return rotation;
	}
	/**
	 * what actions player takes every frame
	 */
	protected void frameCall()
	{
		
	}
}