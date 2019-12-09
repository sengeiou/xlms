package com.xl.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xl.xyl2.XLContext;
import com.xl.xyl2.mvp.view.activity.XLMain;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        findViewById(R.id.btn_lauch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(XLContext.isInit()){
                    XLMain.luach(getApplicationContext());
                }else{
                    Toast.makeText(getBaseContext(),"请在Application中初始化XLContext",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
