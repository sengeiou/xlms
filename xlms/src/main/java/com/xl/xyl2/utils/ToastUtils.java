package com.xl.xyl2.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtils {

    public static void show(final Context context, final String text) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
