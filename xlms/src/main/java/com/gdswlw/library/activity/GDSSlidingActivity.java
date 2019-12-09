package com.gdswlw.library.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.gdswlw.library.widget.slidingmenu.SlidingActivityBase;
import com.gdswlw.library.widget.slidingmenu.SlidingActivityHelper;
import com.gdswlw.library.widget.slidingmenu.lib.SlidingMenu;
import com.xl.xyl2.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A side menu Activity
 * @ClassName: GDSSlidingActivity
 * @Description: TODO
 * @author shihuanzhang 2335946896@qq_com
 */
public abstract class GDSSlidingActivity extends GDSBaseActivity implements
		SlidingActivityBase {

	private SlidingActivityHelper mHelper;
	protected SlidingMenu slidingMenu;
	@IntDef({SlidingMenu.LEFT,SlidingMenu.LEFT_RIGHT,SlidingMenu.RIGHT})
	@Retention(RetentionPolicy.SOURCE)
	public @interface dir {}
	private final int from = 100,to=500;
	@Override
	public void onCreateBefore(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateBefore(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		slidingMenu = getSlidingMenu();
		
	}


	/**
	 * Add a view  not affected by sliding Menu
	 * @param v
     */
	protected void addIgnoredView(@NonNull  View v) {
		getSlidingMenu().addIgnoredView(v);
	}
	
	protected void setSlidingMenu(@dir int dir, int slidingOffset,@LayoutRes  int contentViewId, boolean isShowShadow) {
		setSlidingMenu(dir,slidingOffset,
				include(contentViewId),isShowShadow);
	}

	/**
	 * setSlidingMenu
	 * @param dir direction (SlidingMenu.LEFT or SlidingMenu.RIGHT)
	 * @param slidingOffset
	 * @param contentView
	 */
	protected void setSlidingMenu(@dir int dir, int slidingOffset,
								  @NonNull View contentView, boolean isShowShadow) {
		slidingMenu.setMode(dir);
		if (isShowShadow) {
			switch (dir) {
			case SlidingMenu.LEFT:
				slidingMenu.setShadowDrawable(R.drawable.shadow_left);
				break;
			case SlidingMenu.RIGHT:
				slidingMenu.setShadowDrawable(R.drawable.shadow_right);
				break;
			}
			slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		}
		slidingMenu.setMode(dir);;
		slidingMenu.setBehindOffset(slidingOffset);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setBehindContentView(contentView);
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, 0);
	}

	

	/**
	 * Set the left and right side SlidingMenu menu
	 * @param leftContentViewId
	 * @param rightContentViewId
	 * @param isShowShadow Whether display side shadow
	 */
	protected void setTwoSlidingMenu(int slidingOffset, @LayoutRes  int leftContentViewId,
									 @LayoutRes int rightContentViewId,boolean isShowShadow) {
		setTwoSlidingMenu(slidingOffset,include(leftContentViewId)
				,include(rightContentViewId),isShowShadow);
	}

	/**
	 * Set the left and right side SlidingMenu menu
	 * @param slidingOffset
	 * @param leftContentView
	 * @param rightContentView
	 * @param isShowShadow Whether display side shadow
	 */
	protected void setTwoSlidingMenu(int slidingOffset, View leftContentView,
									 View rightContentView, boolean isShowShadow) {
		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		if (isShowShadow) {
			slidingMenu.setShadowDrawable(R.drawable.shadow_left);
			slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);
			slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		}
		slidingMenu.setBehindOffset(slidingOffset);//设置划出时主页面显示的剩余宽度
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 触摸范围
		setBehindContentView(leftContentView);
		slidingMenu.setSecondaryMenu(rightContentView);
		slidingMenu.setFadeEnabled(true);// 滑动时渐变
		slidingMenu.setFadeDegree(0.35f);// 滑动时的渐变程度
		slidingMenu.attachToActivity(this, 0);
	}
	
	@Override
	public View findViewById(@IdRes  int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	@Override
	public void setContentView(@LayoutRes  int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(@NonNull View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(@NonNull View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	public void setBehindContentView(@LayoutRes  int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(@NonNull View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	public void setBehindContentView(@NonNull View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	public void toggle() {
		if(slidingMenu.isMenuShowing()){
			showContent();
		}else{
			showMenu();
		}
	}

	public void showContent() {
		slidingMenu.showContent(true);
	}

	public void showMenu() {
		// mHelper.showMenu();
		slidingMenu.showMenu(true);
	}

	public void showSecondaryMenu() {
		slidingMenu.showSecondaryMenu();
	}

	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b)
			return b;
		return super.onKeyUp(keyCode, event);
	}

}
