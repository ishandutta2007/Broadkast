package com.examples.ffmpeg4android_demo;



import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.netcompss.ffmpeg4android_client.BaseWizard;
import com.netcompss.ffmpeg4android_client.Prefs;


public class DemoClient extends BaseWizard {
	

	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      _prefs = new Prefs();
	      _prefs.setContext(this);
	      
	      // this will copy the license file and the demo video file.
	      // to the videokit work folder location.
	      // without the license file the library will not work.
	      copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();

	      setContentView(R.layout.ffmpeg_demo_client);
	      
	      Button invoke =  (Button)findViewById(R.id.invokeButton);
	      invoke.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					EditText commandText = (EditText)findViewById(R.id.CommandText);
					String commandStr = commandText.getText().toString();
				
					//Log.i(Prefs.TAG, "Overriding the command with hard coded command");
					//commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vf transpose=1 -s 160x120 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -b 2097k /sdcard/videokit/vid_trans.mp4";
					
					// complex command should be used in cases sub-commands and embedded commands (for example quotations inside a command).
					//String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental", "-vf", "crop=iw/2:ih:0:0,split[tmp],pad=2*iw[left]; [tmp]hflip[right]; [left][right] overlay=W/2", "-vb", "20M", "-r", "23.956", "/sdcard/videokit/out_complex.mp4"};
					
					////////////////////////////////////////////////////////////////////////////////
					////// commands to needed to run the transcoding, only
					////// the setCommand and runTranscoding are mandatory.
					////// All the other commands are optional
					setCommand(commandStr);
					//setCommandComplex(complexCommand);

					///optional////6
					//setOutputFilePath( Environment.getExternalStorageDirectory().getAbsolutePath() );
					setProgressDialogTitle("Exporting As MP4 Video");
					setProgressDialogMessage("Depends on your video size, it can take a few minutes");
					setNotificationIcon(R.drawable.icon2);
					setNotificationMessage("Demo is running...");
					setNotificationTitle("Demo Client");
					setNotificationfinishedMessageTitle("Demo Transcoding finished");
					setNotificationfinishedMessageDesc("Click to play demo");
					setNotificationStoppedMessage("Demo Transcoding stopped");
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
