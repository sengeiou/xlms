package com.xl.xyl2.play;

import java.io.Serializable;

/**
 * Created by Afun on 2019/10/22.
 */

public class DownloadInfo implements Serializable {
    private long totalSize;//总大小
    private long downloadSize;//已下载大小
    private int tid;//终端id

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize += totalSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize += downloadSize;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getProgress(){
        final double dd = downloadSize / (totalSize * 1.0);
        int val = (int) (dd * 100);
        return val;
    }

    public  double pers = 1048576; //1024*1024


    /**
     * 单位格式转换
     * @param size
     * @return
     */
    public  String sizeFormatNum2String(long size) {
        String s = "";
        if(totalSize <= 0){
            s =  "0";
            return s;
        }
        if(size>1024*1024)
            s = String.format("%.2f", (double)size/pers)+"M";
        else
            s = String.format("%.2f", (double)size/(1024))+"KB";
        return s;
    }

    /**
     * 总下载量
     * @return
     */
    public String getTotalSizeString(){
        return sizeFormatNum2String(totalSize);
    }

    /**
     * 设置完成
     */
    public void setFinish(){
        downloadSize = totalSize;
    }

    /**
     * 是否完成
     * @return
     */
    public boolean isFinish(){
        return (totalSize > 0) && (downloadSize == totalSize);
    }
}
