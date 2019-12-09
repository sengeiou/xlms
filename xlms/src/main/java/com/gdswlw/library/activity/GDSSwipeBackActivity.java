package com.gdswlw.library.activity;
import android.os.Bundle;
import android.view.View;

import com.gdswlw.library.widget.swipebacklayout.SwipeBackActivityBase;
import com.gdswlw.library.widget.swipebacklayout.SwipeBackActivityHelper;
import com.gdswlw.library.widget.swipebacklayout.lib.SwipeBackLayout;
import com.gdswlw.library.widget.swipebacklayout.lib.Utils;

/**
 * support functions of sideslip to finish activity
 * @ClassName: SwipeBackActivity
 * @Description: TODO
 * @author shihuanzhang 2335946896@qq_com
 */
public abstract class GDSSwipeBackActivity extends GDSBaseActivity implements
		SwipeBackActivityBase {
	public SwipeBackActivityHelper mHelper;
	public enum SwipeDir {
		LEFT, RIGHT, BOTTOM, LEFT_RIGHT_BOTTOM
	};

	@Override
	public void onCreateBefore(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		setSwipeDirection(SwipeDir.LEFT);

	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v == null && mHelper != null)
			return mHelper.findViewById(id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		Utils.convertActivityToTranslucent(this);
		getSwipeBackLayout().scrollToFinishActivity();
	}

	/**
	  * Set the direction
	  * @Title: setSwipeDirection
	  * @param  dir direction
	 */
	public void setSwipeDirection(SwipeDir dir) {
		int edgeFlag = 0;
		switch (dir) {
		case LEFT:
			edgeFlag = SwipeBackLayout.EDGE_LEFT;
			break;
		case RIGHT:
			edgeFlag = SwipeBackLayout.EDGE_RIGHT;
			break;
		case BOTTOM:
			edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
			break;
		case LEFT_RIGHT_BOTTOM:
			edgeFlag = SwipeBackLayout.EDGE_ALL;
			break;
		}
		getSwipeBackLayout().setEdgeTrackingEnabled(edgeFlag);
	}

}
