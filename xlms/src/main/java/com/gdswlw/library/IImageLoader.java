package com.gdswlw.library;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by shihuanzhang on 2017-12-18.
 */

public interface IImageLoader {
    void loadImage(ImageView imageView, String url);
    void loadImage(ImageView imageView, File file);
}
