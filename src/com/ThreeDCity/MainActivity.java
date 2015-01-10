package com.ThreeDCity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
/*
 * Activity will only deal with starting control, and pausing etc.
 * 
 */
public class MainActivity extends Activity implements SensorEventListener 
{
	Controller control;
	SensorManager mSensorManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        control = new Controller(this, this, getScreenDimensions());
		setContentView(control.graphics);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }
    private double[] getScreenDimensions()
    {
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		double[] dims = {getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels};
		return dims;
    }

    @TargetApi(9)
    public void onSensorChanged(SensorEvent event)
    {
    	control.player.frameCall();
    	float rotationMatrix[];      
    	if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
    	{      
    	       rotationMatrix=new float[16];
    	       mSensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
    	       getOrientation(rotationMatrix);
    	       control.graphics.postInvalidate();
    	}
     }
    private void getOrientation(float[] rotationMatrix)
    {
	    float[] values = new float[3];
	    SensorManager.getOrientation(rotationMatrix, values);
	    double azimuth = values[0];
	    double pitch = values[1];
	    double roll = values[2];
	    control.player.zRotation = roll - Math.PI/2; // y axis
	    control.player.hRotation = azimuth; // y axis
	    control.player.tiltRotation = pitch; // x axis
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume()
    {
       super.onResume();
       mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), mSensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause()
    {
       super.onStop();
       mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }
}
