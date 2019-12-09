package com.gdswlw.library.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gdswlw.library.activity.GDSBaseActivity;

import java.util.HashMap;

public abstract class BaseTabFragment extends GDSBaseActivity{
	private final int NULL_ID = -1;
	int containerId = NULL_ID;//容器的ID
	FragmentManager fragmentManager;
	public Fragment getCurrrentFragment() {
		return currrentFragment;
	}
	Fragment currrentFragment;


	public int getContainerId() {
		return containerId;
	}
	private void setCurrentContent(Fragment f){
		this.currrentFragment = f;
	}
	public abstract int containerId();
	
	public abstract void initView();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		if (getLayout() != 0) {
			setContentView(getLayout());
		}
		containerId = containerId();
		fragmentManager = getSupportFragmentManager();
		initView();
	}
	public HashMap<String, TabButton<Fragment>> getButtons() {
		return buttons;
	}

	public void setButtons(HashMap<String, TabButton<Fragment>> buttons) {
		this.buttons = buttons;
	}

	private HashMap<String, TabButton<Fragment>> buttons = new HashMap<String,  TabButton<Fragment>>();

	//通过View对象创建一个tab按钮
	public void addTabButtonTag(String tag, Fragment fragment) {
		putToButtons(new TabButton<Fragment>(tag, fragment));
	}

	//将tab按钮添加到按钮集合
	private void putToButtons( TabButton<Fragment> tabButton){
		buttons.put(tabButton.getTag(), tabButton);
	}


	/**
	 * change content by tag
	 * @param tag
	 */
	public void changeContent(String tag){
		if(containerId == NULL_ID){
			throw new IllegalArgumentException("Please provide the correct container ID");
		}
		if(buttons.containsKey(tag)){
			setContent(buttons.get(tag));
		}else{
			toastShort("no content for this tag");
		}
	}

	/**
	 * has content with viewId
	 * @param viewId
	 * @return
	 */
	public boolean hasId(int viewId){
		return  buttons.containsKey(String.valueOf(viewId));
	}


	/**
	 *
	 * @param button
	 */
	private void setContent(TabButton<Fragment> button){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if(currrentFragment!=null){
			transaction.hide(currrentFragment);
		}
		Fragment fragment = button.getAction();
		if(!fragment.isAdded()){
			transaction.add(containerId, fragment);
		}
		transaction.show(fragment).commit();
		setCurrentContent(fragment);
	}


	@Override
	public void initUI() {

	}

	@Override
	public void regUIEvent() {

	}
}
