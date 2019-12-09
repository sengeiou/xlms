package com.xl.xyl2.mvp.view.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.gdswlw.library.activity.GDSBaseActivity;
import com.gdswlw.library.adapter.BaseRecycleAdapter;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.R;

import java.util.List;

/**
 * Created by Afun on 2019/10/8.
 */

public class ActivityAllApps extends GDSBaseActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    BaseRecycleAdapter<ResolveInfo> recycleAdapter;
    @Override
    public void initUI() {
        recyclerView = viewId(R.id.rv_all_apps);
        recyclerView.setLayoutManager (new GridLayoutManager(getBaseContext(),6,GridLayoutManager.VERTICAL,false));
        final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(mIntent, 0);

        recycleAdapter = new BaseRecycleAdapter<ResolveInfo>(resolveInfos) {
            @Override
            protected void bindData(BaseViewHolder holder, int position) {
                holder.setText(R.id.tv_app_name,resolveInfos.get(position).loadLabel(getPackageManager()).toString());
                ((ImageView)holder.getView(R.id.iv_icon)).setImageDrawable(resolveInfos.get(position).loadIcon(getPackageManager()));
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_layout_app;
            }
        };
        recycleAdapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ResolveInfo appInfo = resolveInfos.get(position);
                if(appInfo.activityInfo.packageName.equals("com.xl.xyl2")){
                    //如果当前app在播放则返回到当前app
                    if(ActivityShow.instatnce != null){
                        finish();
                       return;
                    }
                }
                Intent mIntent = getPackageManager().getLaunchIntentForPackage(appInfo.activityInfo.packageName);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(mIntent);
                } catch (ActivityNotFoundException e) {
                    UIKit.dLog(e.getMessage());
                }
            }
        });
        recyclerView.setAdapter(recycleAdapter);
        toolbar = viewId(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_apps));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void regUIEvent() {

    }

    @Override
    public int getLayout() {
        return R.layout.layout_all_app;
    }
}
