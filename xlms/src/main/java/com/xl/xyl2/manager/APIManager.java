package com.xl.xyl2.manager;

import android.content.Context;

/**
 * Created by Afun on 2019/10/11.
 */

public class APIManager {
    private static Context context;
    public static void init(Context context){
        if(context == null){
            APIManager.context = context;
        }
    }



}
