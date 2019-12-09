package com.gdswlw.library.dialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xl.xyl2.R;


public class AppProgressDialog extends ProgressDialog{
	private String message;
	private TextView messageTextView;
	private int layoutId,messageId;
	
	public AppProgressDialog(Activity activity,int layoutId,int messageId) {
		super(activity, R.style.my_dialog_style);
		this.layoutId = layoutId;
		this.messageId  = messageId;
		message = "";
	}
	public void setMessage(String message){
		if(message != null) {
			this.message = message;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		if(findViewById(messageId) instanceof TextView){
			messageTextView = (TextView) findViewById(messageId);
			messageTextView.setText(message);
		}
		
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		if(messageTextView!=null && !"".equals(message.trim())){
			messageTextView.setText(message);
			messageTextView.setVisibility(View.VISIBLE);
		}else{
			messageTextView.setVisibility(View.GONE);
		}
	}

	
}
