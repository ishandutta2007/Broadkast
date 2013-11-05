package com.example.screenshot;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;

import com.netcompss.ffmpeg4android_client.BaseWizard;
import com.netcompss.ffmpeg4android_client.Prefs;

public class Screenshot extends BaseWizard {
	
	String fbpath = "/dev/graphics/fb0";
	File tempBuff;
	File images;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get destination for output
    	images = new File(Environment.getExternalStorageDirectory(), "screenshots");
    	
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //Handles take screenshot event from pressing button
    public void takeScreenshot( View view ) throws RemoteException, IOException, InterruptedException
    {
    	//First copy contents of /dev/graphics/fb0
    	//to temp file
    	
    	Process p = Runtime.getRuntime().exec("su -c cat /dev/graphics/fb0 > " + images + "/tmpbuf");
    	p.waitFor();
    	
    	//create output filename
    	String outfilename = String.format("scan%d.png", System.currentTimeMillis());
    	
    	File outfile = new File( images.getPath(), outfilename);
    	
    	String out = outfile.getPath();
    	//build ffmpeg command
    	String command = "ffmpeg -vcodec rawvideo -f rawvideo -pix_fmt rgba -s 736x1280 -i /sdcard/screenshots/tmpbuf -f -image2 -vcodec png /sdcard/screenshots/out.png";
    	
    	//execute ffmpeg command
    	remoteService.setFfmpegCommand(command);
    	runWithCommand(command);
    	
    	
    	//have to check directory to see if it worked
    	
    }
    
}
