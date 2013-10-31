package com.example;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.netcompss.ffmpeg4android.LicenseCheckJNI;
import com.netcompss.ffmpeg4android.R;
import com.netcompss.ffmpeg4android_client.BaseWizard;
import com.netcompss.ffmpeg4android_client.FileUtils;
import com.netcompss.ffmpeg4android_client.Prefs;
import com.netcompss.ffmpeg4android_client.TranscodeBackground;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


public class DemoClient extends BaseWizard {
	

	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      _prefs = new Prefs();
	      _prefs.setContext(this);
      
	      // if you want to change the default work location (/sdcard/videokit/) use the uncomment the below method.
	      // It must be defined before calling the copyLicenseAndDemoFilesFromAssetsToSDIfNeeded method,
	      // in order for this method to copy the assets to the correct location.
	      //setWorkingFolder("/sdcard/videokit3/");
	      
	      // this will copy the license file and the demo video file 
	      // to the videokit work folder location 
	      // without the license file the library will not work
	      copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
	      
	      if (Prefs.transcodingIsRunning) {
				Log.i(Prefs.TAG, "Currently transcoding is running, not running.");
				Toast.makeText(this, getString(R.string.trascoding_running_background_message), Toast.LENGTH_LONG).show();
				finish();
				return;
		  }
	      
	      setContentView(R.layout.ffmpeg_demo_client2);
	      
	      Button invoke =  (Button)findViewById(R.id.invokeButton);
	      invoke.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					EditText commandText = (EditText)findViewById(R.id.CommandText);
					
					String commandStr = commandText.getText().toString();
					
					
					//String[] complexCommand = {"ffmpeg","-threads", "0", "-y" ,"-i", "/sdcard/videokit/sample.mp4","-strict","experimental", "-vf", "crop=iw/2:ih:0:0,split[tmp],pad=2*iw[left]; [tmp]hflip[right]; [left][right] overlay=W/2", "-vcodec", "mpeg4", "-vb", "20M", "-r", "23.956", "/sdcard/videokit/out.mp4"};
					
					////////////////////////////////////////////////////////////////////////////////
					////// Only the setCommand (setCommandComplex) and runTranscoding are mandatory.
					////// All the other commands are optional
					
					setCommand(commandStr);
					//setCommandComplex(complexCommand);
					        			
					///optional////
					setOutputFilePath("/sdcard/videokit/out.mp4");
					setProgressDialogTitle("Transcoding...");
					setProgressDialogMessage("Depends on your video size, transcoding can take a few minutes");
					setNotificationIcon(R.drawable.notification_icon);
					setNotificationMessage("Demo is running...");
					setNotificationTitle("DemoClient");
					setNotificationfinishedMessageTitle("Transcoding finished");
					setNotificationfinishedMessageDesc("Click to play");
					setNotificationStoppedMessage("Transcoding stopped");
					///////////////
				
					runTranscoing();
        			///////////////////////////////////////////////////////////////////////////////
        			
        			
        			
        			
				}
			});
	      
	      Button showLog =  (Button)findViewById(R.id.showLastRunLogButton);
	      showLog.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					startAct(com.netcompss.ffmpeg4android_client.ShowFileAct.class);				
				}
			});
	      
	      
	}
	
	
	 


}
