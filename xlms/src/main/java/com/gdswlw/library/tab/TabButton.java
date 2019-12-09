package com.gdswlw.library.tab;

public final class TabButton<T> {
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	protected String tag;
	/**
	 * Could be an Intent or a Fragment
	 */
	private T action;
	public T getAction() {
		return this.action;
	}
	public void setAction(T action) {
		this.action = action;
	}
	
	public TabButton(String tag,T action){
		setTag(tag);
		setAction(action);
	}
}
