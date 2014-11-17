package com.research.poster;

import rajawali.util.RajLog;
import rajawali.vuforia.RajawaliVuforiaActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends RajawaliVuforiaActivity implements MyRenderer.InitListener{
	private MyRenderer	mRenderer;
	private RajawaliVuforiaActivity mUILayout;
	private RelativeLayout 	mUIView;
	private TextView		mInstruView;
	private LinearLayout 	mLoadView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		useCloudRecognition(false);
		startVuforia();
	}

	@Override
	protected void setupTracker() {
		int result = initTracker(TRACKER_TYPE_MARKER);
		if (result == 1) {
			result = initTracker(TRACKER_TYPE_IMAGE);
			if (result == 1) {
				super.setupTracker();
			} else {
				RajLog.e("Couldn't initialize image tracker.");
			}
		} else {
			RajLog.e("Couldn't initialize marker tracker.");
		}
	}
	@Override
	protected void initApplicationAR()
	{
		super.initApplicationAR();		
		createImageMarker("ARATL.xml");
		

	}

	@Override
	protected void initRajawali() {
		super.initRajawali();
		mRenderer = new MyRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		mRenderer.registerListener(this);
		super.setRenderer(mRenderer);	
		
        mUILayout = this;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUIView = (RelativeLayout)inflater.inflate(R.layout.fragment_main, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);  

        mUILayout.addContentView(mUIView, layoutParams);	
        
        mLoadView = (LinearLayout)findViewById(R.id.loading_message);
        mInstruView = (TextView)findViewById(R.id.instruction_message);
        mInstruView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onInitComplete() {
		try {
				// code runs in a thread
				runOnUiThread(new Runnable() {
				   @Override
				   public void run() {
					   mLoadView.setVisibility(View.INVISIBLE);
					   mInstruView.setVisibility(View.VISIBLE);
				   }
				});
		  } catch (final Exception ex) {
		      ex.printStackTrace();
		  }
		
	} 

}
