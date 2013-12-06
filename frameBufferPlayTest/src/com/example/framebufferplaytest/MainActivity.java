package com.example.framebufferplaytest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity
{
	final String TAG = "MAIN"

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onClickRecord()
    {
    	Log.i(TAG, "Clicked record button");
    	Intent i = new Intent(this, RecordActivity.class);
    	startActivity(i);
    }
    
    public void onClickPlay()
    {
    	Log.i(TAG, "Clicked play button");
    	Intent i = new Intent(this, PlayActivity.class);
    	startActivity(i);
    	
    }
}
