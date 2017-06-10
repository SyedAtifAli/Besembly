package com.shamansoft.besemblymobile;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity  extends Activity
{
	public static Activity activity = null;
	
	private FileDialog fileDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		activity = this;
		
		LogoPlay();
	}
	
	public void newBsm(View v) 
	{
		setContentView(R.layout.activity_new_bsm);
	}
	public void openBsm(View v) 
	{
		File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        fileDialog = new FileDialog(this, mPath);
        fileDialog.setFileEndsWith(".bsm");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() 
        {
            public void fileSelected(File file) 
            {
                Log.d(getClass().getName(), "selected file " + file.toString());
            }
        });
        fileDialog.showDialog();
	}
	public void instructionList(View v) 
	{
		setContentView(R.layout.activity_instruction_list);
	}
	public void community(View v) 
	{
		setContentView(R.layout.activity_community);
	}
	public void aboutUs(View v) 
	{
		setContentView(R.layout.activity_about_us);
	}
	
	public void save(View v)
	{
		
	}
	public void run(View v)
	{
		
	}
	
	public static void AnimPressPlay(View v)
  	{
  		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.3f);
  		alphaDown.setDuration(250);
  		alphaDown.setFillAfter(true);
  		AlphaAnimation alphaUp = new AlphaAnimation(0.3f, 1.0f);
  		alphaUp.setDuration(250);
  		alphaUp.setFillAfter(true);
  	    v.startAnimation(alphaUp);
  	}
 	
 	public void LogoPlay()
  	{
 		setContentView(R.layout.logo);
 		
 	    final View logoView = (ImageView) findViewById(R.id.logo);
 	    
 	    final AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.0f);
 	    alphaDown.setDuration(250);
 	    alphaDown.setFillAfter(true);
 	    alphaDown.setAnimationListener(new Animation.AnimationListener()
	   	{
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
			public void onAnimationEnd(Animation arg0) 
			{
				setContentView(R.layout.activity_main);
			}
		    	
	   	});
 		
 	    final AlphaAnimation alphaUp = new AlphaAnimation(0.0f, 1.0f);
 	    alphaUp.setDuration(1250);
 	    alphaUp.setFillAfter(true);
 	    alphaUp.setAnimationListener(new Animation.AnimationListener()
    	{
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
			public void onAnimationEnd(Animation arg0) 
			{
				logoView.startAnimation(alphaDown);
			}
 	    	
    	});
  	    logoView.startAnimation(alphaUp);
  	}
}
