package com.gdswlw.library.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.gdswlw.library.activity.GDSBaseActivity;

import java.util.HashMap;

public abstract class BaseTabViewPagerFragment extends GDSBaseActivity{
	ViewPager viewPager;
	TabFragmentPagerAdapter tabFragmentPagerAdapter;
	FragmentManager fragmentManager;
	public Fragment getCurrrentFragment() {
		return currrentFragment;
	}
	Fragment currrentFragment;

	protected abstract ViewPager viewPager();
	private void setCurrentContent(Fragment f){
		this.currrentFragment = f;
	}

	public abstract void initView();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		if (getLayout() != 0) {
			setContentView(getLayout());
			viewPager = viewPager();
			if(viewPager != null){
				fragmentManager = getSupportFragmentManager();
                tabFragmentPagerAdapter = new TabFragmentPagerAdapter(fragmentManager);
                viewPager.setAdapter(tabFragmentPagerAdapter);
				initView();
			}
		}

	}
	public HashMap<String, TabButton<Fragment>> getButtons() {
		return buttons;
	}

	public void setButtons(HashMap<String, TabButton<Fragment>> buttons) {
		this.buttons = buttons;
	}

	private HashMap<String, TabButton<Fragment>> buttons = new HashMap<String,  TabButton<Fragment>>();

	//通过View对象创建一个tab按钮
	public void addTabButtons(Fragment... fragments) {
	    for(Fragment fragment:fragments){
            putToButtons(new TabButton<>(String.valueOf(getKeySize()), fragment));
        }
        tabFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(fragments.length);
	}

	private int getKeySize(){
	    return  buttons.keySet().size();
    }

	//将tab按钮添加到按钮集合
	private void putToButtons( TabButton<Fragment> tabButton){
	    if(! buttons.containsKey(tabButton.getTag())){
            buttons.put(tabButton.getTag(), tabButton);
        }

	}


	/**
	 * change content by tag
	 * @param tag
	 */
	public void changeContent(String tag){
		if(buttons.containsKey(tag)){
			setContent(Integer.parseInt(tag));
		}else{
			toastShort("no content for this tag");
		}
	}

	/**
	 * has content with viewId
	 * @param index
	 * @return
	 */
	public boolean hasIndex(int index){
		return  buttons.containsKey(String.valueOf(index));
	}


	/**
	 *
	 * @param index
	 */
	private void setContent(int index){
	    viewPager.setCurrentItem(index);
	}


	@Override
	public void initUI() {

	}

	@Override
	public void regUIEvent() {

	}

	 class TabFragmentPagerAdapter extends FragmentPagerAdapter {
		private FragmentManager mfragmentManager;

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			return getButtons().get(String.valueOf(index)).getAction();
		}

		@Override
		public int getCount() {
			return getButtons().keySet().size();
		}
	}
}
