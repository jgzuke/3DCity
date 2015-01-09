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
    	float rotationMatrix[];      
    	switch(event.sensor.getType())
    	{      
    		case Sensor.TYPE_ACCELEROMETER:
            //outputX.setText("x:"+Float.toString(event.values[0]));
            //outputY.setText("y:"+Float.toString(event.values[1]));
            //outputZ.setText("z:"+Float.toString(event.values[2]));
    		break;
    	  case Sensor.TYPE_ROTATION_VECTOR:
    	       rotationMatrix=new float[16];
    	       mSensorManager.getRotationMatrixFromVector(rotationMatrix,event.values);
    	       getOrientation(rotationMatrix);
    	       break;
    	 }//switch case ends
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
	    Log.e("mine", "Az: " + Double.toString(azimuth*57.2958));
	    Log.e("mine", "Pi: " + Double.toString(pitch*57.2958));
	    Log.e("mine", "Ro: " + Double.toString(roll*57.2958));
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
       mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mSensorManager.SENSOR_DELAY_GAME);
       mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), mSensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onStop()
    {
       super.onStop();
       mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
       mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }
}
