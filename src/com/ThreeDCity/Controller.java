package com.ThreeDCity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/*
 * Control runs frames, controls graphics redraw, etc
 * 
 */
public final class Controller
{
	protected Graphics graphics;
	protected Player player;
	protected Objects objects;
	private Activity activity;
	private Context context;
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context contextSet, MainActivity activitySet, double[] dimensions)
	{
		context = contextSet;
		activity = activitySet;
		player = new Player(this);
		objects = new Objects(this);
		graphics = new Graphics(contextSet, this, dimensions);
		graphics.setOnTouchListener(player);
	}
}