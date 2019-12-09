package com.gdswlw.library.activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.xl.xyl2.R;


/**
  * @ClassName: SplashBaseActivity
  * @author shihuanzhang 2335946896@qq_com
 */
public abstract class SplashBaseActivity extends FragmentActivity{
	private final long minTime = 1500,maxtime = 5000;
	/**
	 * Returns an ImageView object you can  settings your app splash image
 	 * @param imageView
     */
	protected abstract void setupImageView(ImageView imageView);
	/**
	 * Do you want to do ?
	 */
	protected abstract void loadingFinish();

	/**
	 * Provide a delayTime ,to complete the assigned task
	 * @return
     */
	protected abstract long delayTime();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		setupImageView((ImageView) findViewById(R.id.iv_splash));
		startTask(delayTime());
	}

	
	private void startTask(long time){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				loadingFinish();
			}
		}, (time < minTime ? minTime:(time > maxtime ? maxtime : time)));
	}
	

}
