package com.ThreeDCity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/*
 * Controlls movements nd rotation of view
 */
public final class Player implements OnTouchListener
{
	protected int height = 190; //cm
	protected int x = -200;
	protected int y = 0;
	protected int z = 190; //height of eyes
	protected double hRotation = 0; //looking directly along x axis , -ve is left
	protected double zRotation = 0; //looking directly forward, -ve is down
	protected int looking = -1; 	// -1 is nope
	protected float lastLookX = -1; 	// -1 is nope
	protected float lastLookY = -1; 	// -1 is nope
	protected int moving = -1;	// -1 is false
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
		if(hRotation>Math.PI*2) hRotation -= Math.PI*2;
		if(hRotation<0) hRotation += Math.PI*2;
		if(zRotation>Math.PI/3) zRotation = Math.PI/3;
		if(zRotation<-Math.PI/3) zRotation = -Math.PI/3;
	}
	public boolean clickedMove(float x, float y)
	{
		return (x < 100 && y > 0);
	}
	@Override
	public boolean onTouch(View v, MotionEvent e)
	{
		int actionMask = e.getActionMasked();
        switch (actionMask){
        case MotionEvent.ACTION_DOWN:
        	if(clickedMove(e.getX(), e.getY()))
        	{
        		moving = e.getPointerId(0);
        	} else
        	{
        		looking = e.getPointerId(0);
        		lastLookX = e.getX();
        		lastLookY = e.getY();
        	}
        break;
        case MotionEvent.ACTION_MOVE:
            if(looking != -1)
            {
            	hRotation += (e.getX(e.findPointerIndex(looking))-lastLookX)/1000;
            	zRotation -= (e.getY(e.findPointerIndex(looking))-lastLookY)/1000;
            	lastLookX = e.getX(e.findPointerIndex(looking));
        		lastLookY = e.getY(e.findPointerIndex(looking));
            }
            if(moving != -1)
            {
	            moveForward();
            }
        break;
        case MotionEvent.ACTION_UP:
        	looking = -1;
        	moving = -1;
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	if(e.getPointerId(e.getActionIndex()) == looking)
        	{
        		looking = -1;
        	}
        	if(e.getPointerId(e.getActionIndex()) == moving)
        	{
        		moving = -1;
        	}
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	if(clickedMove(e.getX(), e.getY()))
        	{
        		moving = e.getPointerId(e.getActionIndex());
        	} else
        	{
        		looking = e.getPointerId(e.getActionIndex());
        		lastLookX = e.getX(e.getActionIndex());
        		lastLookY = e.getY(e.getActionIndex());
        	}
        break;
        }
        return true;
	}
	private void moveForward() {
		x += Math.cos(hRotation)*5;
		y += Math.sin(hRotation)*5;
	}
}