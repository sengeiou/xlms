package com.gdswlw.library.http;

import java.io.File;

/**
 * Created by shihuanzhang on 2017-11-13.
 */

public interface CallbackDownload {
    /**
     *  onSuccess
     * @param file downloaded file
     */
    void onSuccess(File file);

    /**
     * onFailure
     * @param throwable failure message
     */
    void onFailure(Throwable throwable);

    /**
     * listening file download progress
     * @param bytesWritten current download progress(byte)
     * @param totalbytes The file total size(byte)
     */
    void progress(long bytesWritten, long totalbytes);
}
