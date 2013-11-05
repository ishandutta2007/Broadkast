package com.example.screenshot;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.netcompss.ffmpeg4android_client.BaseWizard;
import com.netcompss.ffmpeg4android_client.Prefs;

public class Screenshot extends BaseWizard {
	String TAG = "Screenshot";
	String fbpath = "/dev/graphics/fb0";
	File tempBuff;
	File images;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //copying code from demo client
	    _prefs = new Prefs();
	    _prefs.setContext(this);
	      
        //get destination for output
    	images = new File(getExternalFilesDir(null), "screenshots");
    	
    	//creating output file
    	tempBuff = new File( getExternalFilesDir(null), "tempbuf");
    	
    	images.mkdirs();
    	try {
			tempBuff.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //Handles take screenshot event from pressing button
    public void takeScreenshot( View view )
    {
    	//First copy contents of /dev/graphics/fb0
    	//to temp file
    	long startTime = System.currentTimeMillis();
    	int numShots = 100;
    	
	   // for( int i=0; i < numShots; i++){
	    	
	    	Process p = null;
			try {
				p = Runtime.getRuntime().exec("su -c cat " + fbpath + " > " + tempBuff.getAbsolutePath());
				p.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e( TAG, "cat failed");
			} catch ( InterruptedException e2) {
				e2.printStackTrace();
				Log.e(TAG, "p.waitFor() interrupted");
			}
	    	
	    	
	    	if( p.exitValue() < 0 )
	    	{
	    		System.exit( -1 );
	    	}
	    	
	    	//create output filename
	    	String outfilename = String.format("scan%d.png", System.currentTimeMillis());
	    	
	    	File outfile = new File( images.getPath(), outfilename);
	    	
	    	//build ffmpeg command
	    	String command = "ffmpeg -vframes 1 -vcodec rawvideo -f rawvideo -pix_fmt rgba -s 736x1300 -i " + tempBuff.getAbsolutePath()+ " -f image2 -vcodec png "+ outfile.getAbsolutePath();
	    	
	    	setCommand(command);
			runTranscoing();
	    	
	    	//startAct(com.netcompss.ffmpeg4android_client.ShowFileAct.class);	
	  //  }
	    	
		long endtime = System.currentTimeMillis();
		
		Log.i(TAG, "Total running time: " + (endtime - startTime) + "\nTime per screenshot: " + (endtime-startTime)/numShots);
    	
    	//have to check directory to see if it worked
    	
    }
    
}
