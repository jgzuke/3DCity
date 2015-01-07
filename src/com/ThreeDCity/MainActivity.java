package com.ThreeDCity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/*
 * Activity will only deal with starting control, and pausing etc.
 * 
 */
public class MainActivity extends Activity
{
	Controller control;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = new Controller(this, this, getScreenDimensions());
		setContentView(control.graphics);
    }
    private double[] getScreenDimensions()
    {
		double[] dims = {getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels};
		return dims;
    }

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
}
