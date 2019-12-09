package com.xl.xyl2.mvp.view.ui;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.R;
import com.xl.xyl2.play.PlayLog;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Afun on 2019/10/21.
 */

public class DifferentDislay extends Presentation {
    List<PlayUnit> playUnits;
    RelativeLayout relativeLayout;
    public DifferentDislay(Context outerContext, Display display, List<PlayUnit> playUnits) {
        super(outerContext,display);
        this.playUnits = playUnits;
    }
    XLImageContainer  xlImageContainer;
    TextView textView;
    TextView tvStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = findViewById(R.id.tv_message);
        relativeLayout = findViewById(R.id.rl_parent);
        tvStop = findViewById(R.id.tv_stop);
        updateDatas(playUnits);
    }

   public void updateDatas(List<PlayUnit> playUnits){
       tvStop.setVisibility(View.GONE);
       this.playUnits = playUnits;
       if(playUnits != null && playUnits.size() > 0){
           textView.setVisibility(View.GONE);
           xlImageContainer = new XLImageContainer(getContext());
           xlImageContainer.loadByDatas(playUnits);
          if(! xlImageContainer.isAttachedToWindow()){
              RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
              relativeLayout.addView(xlImageContainer,layoutParams);
          }
       }else{
           textView.setVisibility(View.VISIBLE);
           textView.setText("There is currently no playable linkage unit");
           if(xlImageContainer!= null &&  xlImageContainer.isAttachedToWindow()){
               relativeLayout.removeView(xlImageContainer);
           }
       }
   }

   public void play(boolean isPlay){
       if(xlImageContainer == null){
           return;
       }
       xlImageContainer.play(isPlay);
       tvStop.bringToFront();
       tvStop.setVisibility(isPlay?View.GONE:View.VISIBLE);
   }
}
