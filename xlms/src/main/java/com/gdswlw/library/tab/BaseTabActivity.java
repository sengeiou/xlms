package com.gdswlw.library.tab;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View.OnClickListener;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public abstract class BaseTabActivity extends TabActivity implements
		OnClickListener {
	public abstract void initView();
	protected TabButton<Intent> current;
	

	public TabButton<Intent> getCurrent() {
		return current;
	}

	//Record the currently selected button
	public void setCurrent(TabButton<Intent> current) {
		this.current = current;
	}
	
	//The set of buttons
	private HashMap<String, TabButton<Intent>> buttons = new HashMap<String, TabButton<Intent>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
	}

	/**
	 * Through the View object to create a TAB button
	 * @param tag
	 * @param intent
     */
	public void addTabButtonByTag(@NonNull String tag, Intent intent) {
		putToButtons(new TabButton<Intent>(tag, intent));
	}


	/**
	 * addTabButtonByView
	 * @param tag
	 * @param cls
     */
	public void addTabButtonByView(@NonNull String tag, Class<? extends  Activity> cls) {
		addTabButtonByTag(tag, intent(cls));
	}

	/**
	 * Add the TAB button to the set button
	 * @param tabButton
     */
	private void putToButtons(TabButton<Intent> tabButton) {
		buttons.put(tabButton.getTag(), tabButton);//键值对保存每个按钮
		setTabContent(tabButton);
	}

	/**
	 * Each view the id of the string is set to the TAB identification <br/>
	 * Identification and collection of the key is the same
	 * @param button
     */
	protected void setTabContent(TabButton<Intent> button) {
		getTabHost().addTab(getTabHost().newTabSpec(button.getTag())
				.setContent(button.getAction()).setIndicator(button.getTag())

		);
	}

	/**
	 * Get intent by Activity class
	 * @param cls
	 * @return
     */
	private Intent intent(Class<? extends Activity> cls) {
		return new Intent(getApplicationContext(), cls);
	}

	/**
	 * Settings TAB by view component  id
	 * @param id
     */
	protected void setTabById(@IdRes  int id) {
		String strId = String.valueOf(id);
		if (buttons.containsKey(strId)) {//前提下必须初始化该id对应Button，否则切换不成功
			getTabHost().setCurrentTabByTag(strId);
			setCurrent(buttons.get(strId));
		}
	}

	/**
	 * Settings TAB by View
	 * @param tag
	 */
	protected void setTabByTag(@NonNull String tag) {
		if (buttons.containsKey(tag)) {
			getTabHost().setCurrentTabByTag(tag);
			setCurrent(buttons.get(tag));
		}
	}

}
