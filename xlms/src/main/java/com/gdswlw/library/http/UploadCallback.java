package com.gdswlw.library.http;

/**
 * Created by shihuanzhang on 2017-12-25.
 */

public interface UploadCallback extends Callback{
    void progress(long bytesWritten, long totalbytes);
}
