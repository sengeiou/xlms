package com.xl.xyl2.mvp.view.ui.webview;

import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by Afun on 2019/9/16.
 */

public class MyWebChromeClient extends WebChromeClient {

    private Context context;
    private ProgressBar progressBar;


    public MyWebChromeClient(Context context, ProgressBar progressBar){
        this.context = context;
        this.progressBar = progressBar;
    }


    //监听进度的回调
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if(newProgress == 100){
            progressBar.setVisibility(View.GONE);
        }else{

            if(progressBar.getVisibility() == View.GONE){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        }
        super.onProgressChanged(view, newProgress);
    }
}