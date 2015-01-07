package com.ThreeDCity;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public final class Graphics extends View
{
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	double screenWidth, screenHeight;
	public Graphics(Context contextSet, double [] dimensions)
	{
		super(contextSet);
		screenWidth = dimensions[0];
		screenHeight = dimensions[1];
	}
	protected void frameCall()
	{
		invalidate();
	}
	@Override
	protected void onDraw(Canvas g)
	{
		
	}
}