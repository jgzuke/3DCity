package com.ThreeDCity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

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
	private Handler mHandler = new Handler();
	protected Runnable frameCaller = new Runnable()
	{
		public void run()
		{
			frameCall();
			mHandler.postDelayed(this, 50);
		}
	};	
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context contextSet, MainActivity activitySet, double[] dimensions)
	{
		context = contextSet;
		activity = activitySet;
		graphics = new Graphics(contextSet, this, dimensions);
		player = new Player(this);
		objects = new Objects(this);
	}
	private void frameCall()
	{
		player.frameCall();
		graphics.frameCall();
	}
}