package com.xl.xyl2.download;

import com.xl.xyl2.play.PlayArea;
import com.xl.xyl2.play.PlayList;
import com.xl.xyl2.play.PlayProgram;
import com.xl.xyl2.play.PlayUnit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载管理类
 */
public class DownLoadManager {

    static Hashtable<Integer, DownLoadManager> curManagerHt = new Hashtable<>();

    public static DownLoadManager getCur(int tid) {
        return curManagerHt.get(tid);
    }

    public static void toDownLoad(int tid, PlayList playList, String temp, String source, DownLoadCallBack downLoadCallBack, int maxths, int startPlayFs) {
        new DownLoadManager(tid, playList, temp, source, downLoadCallBack, maxths, startPlayFs);
    }

    static ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
    static Lock waitDownFilesLock = new ReentrantLock(true);

    class _WaitDownModel {
        public String sourceId;
        public String sourceUrl;
        public long sourceSize;
        public long downLoadSize;
        public boolean isFinish;
        public boolean isDownloading;
        public boolean isOtherDownloading;

        public _WaitDownModel(PlayUnit pu) {
            this.sourceId = pu.sourceId;
            this.sourceSize = pu.sourceSize;
            this.sourceUrl = pu.sourceUrl;
            this.downLoadSize = 0;
            this.isFinish = false;
            this.isDownloading = false;
        }
    }

    public Hashtable<String, _WaitDownModel> files = new Hashtable<>(); //所有文件

    int getSuccessNum() {
        int fs = 0;
        for (Iterator it = files.keySet().iterator(); it.hasNext(); )
            if (files.get(it.next()).isFinish) fs++;
        return fs;
    }

    public boolean isFinish() {
        return getSuccessNum() >= files.size();
    }

    void sucessDownloadFile(_WaitDownModel wdm) {
        waitDownFilesLock.lock();
        try {
            wdm.downLoadSize = wdm.sourceSize;
            wdm.isFinish = true;
            wdm.isDownloading = false;
            files.put(wdm.sourceId, wdm);
            //检查回调
            if (downLoadCallBack == null) return;
            if (startPlayFileNum == -9999) return;//已经促发过
            int fs = getSuccessNum();
            if (fs == files.size()) {
                downLoadCallBack.toPlay(terminalId, true);
                startPlayFileNum = -9999;
                clearTemp();
                return;
            }
            if (startPlayFileNum > 0 && fs >= startPlayFileNum) {
                if (downLoadCallBack.toPlay(terminalId, false))
                    startPlayFileNum = -9999;
            }
        } finally {
            waitDownFilesLock.unlock();
        }
    }

    public _WaitDownModel getWaitDownItem() {
        waitDownFilesLock.lock();
        try {
            if (files == null || files.size() == 0) return null;
            for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
                _WaitDownModel wdm = files.get(it.next());
                if (wdm.isFinish || wdm.isDownloading) continue;
                wdm.isOtherDownloading = false;
                for (Iterator it1 = curManagerHt.keySet().iterator(); it1.hasNext(); ) {
                    DownLoadManager dm = curManagerHt.get(it1.next());

                    _WaitDownModel oep = dm.files.get(wdm.sourceId);
                    if (oep == null) continue;
                    if (oep.isDownloading && !oep.isOtherDownloading) {
                        wdm.downLoadSize = oep.downLoadSize;
                        wdm.isOtherDownloading = true;
                        break;
                    }
                }
                wdm.isDownloading = true;
                return wdm;
            }
            return null;
        } finally {
            waitDownFilesLock.unlock();
        }
    }

    public void resetWaitDownItem(_WaitDownModel wdm) {
        waitDownFilesLock.lock();
        try {
            wdm.isDownloading = false;
        } finally {
            waitDownFilesLock.unlock();
        }
    }

    private int downSynNum = 3;//同时下载数

    private int startPlayFileNum = 0;

    private String tempDir;

    private String sourceDir;

    private DownLoadCallBack downLoadCallBack;

    private int terminalId;

    private PlayList playList;


    private boolean taskisRun = true;

    public void stop() {
        //停止下载任务
        this.taskisRun = false;
        //taskExecutor.shutdownNow();
    }

    public int getProgress() {
        boolean allf = true;
        long tsize = 0, dsize = 0;
        for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
            _WaitDownModel wdm = files.get(it.next());
            if (!wdm.isFinish) allf = false;
            tsize += wdm.sourceSize;
            dsize += wdm.downLoadSize;
        }
        if (allf) return 100;
        int pro = (int) ((double) dsize / (double) tsize * 100);
        return pro > 100 ? 100 : pro;
    }


    void clearTemp() {
        //所有播放列表都出于完成状态
        if (curManagerHt == null) return;
        for (Iterator it = curManagerHt.keySet().iterator(); it.hasNext(); )
            if (!curManagerHt.get(it.next()).isFinish()) return;
        File dir = new File(tempDir);
        if (!dir.exists()) return;
        File[] fs = dir.listFiles();
        if (fs == null || fs.length == 0) return;
        for (File f : fs) f.delete();
    }

    /**
     * 获取远程文件尺寸
     * -1网络或服务器异常
     * 0 文件不存在
     */
    private long getRemoteFileSize(String remoteFileUrl) throws IOException {
        long fileSize = 0;
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
        httpConnection.setRequestMethod("HEAD");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode > 400) return responseCode == 404 ? 0 : -1;
        String sHeader;
        for (int i = 1; ; i++) {
            sHeader = httpConnection.getHeaderFieldKey(i);
            if (sHeader != null && sHeader.equals("Content-Length")) {
                fileSize = Long.parseLong(httpConnection.getHeaderField(sHeader));
                break;
            }
        }
        return fileSize;
    }

    /**
     * 获取节目标识
     * @return
     */
    public String getPid(){
        return playList == null?"":playList.getIdentification();
    }

    public DownLoadManager(int tid, PlayList playList, String temp, String source, DownLoadCallBack downLoadCallBack, int maxths, int startPlayFs) {
        this.terminalId = tid;
        this.playList = playList;
        this.tempDir = temp;
        this.sourceDir = source;
        this.downSynNum = maxths;
        this.downLoadCallBack = downLoadCallBack;
        this.startPlayFileNum = startPlayFs;
        File tp = new File(tempDir);
        if (!tp.exists()) tp.mkdirs();
        File sd = new File(sourceDir);
        if (!sd.exists()) sd.mkdirs();
        if (curManagerHt.get(tid) != null)
            curManagerHt.get(tid).stop();
        curManagerHt.put(tid, this);
        for (PlayProgram pp : playList.getPrograms()) {
            if (pp.getAreas() != null && pp.getAreas().size() > 0) {
                for (PlayArea pa : pp.getAreas()) {
                    if (pa.getType() != 1 && pa.getType() != 2) continue;
                    if(pa.getUnits()==null|| pa.getUnits().size()==0) continue;
                    for (PlayUnit pu : pa.getUnits()) {
                        _WaitDownModel wdm = new _WaitDownModel(pu);
                        files.put(wdm.sourceId, wdm);
                    }
                }
            }
            if (pp.linkUnits != null && pp.linkUnits.size() > 0) {
                //加载联动文件
                for (PlayUnit pu : pp.linkUnits) {
                    _WaitDownModel wdm = new _WaitDownModel(pu);
                    files.put(wdm.sourceId, wdm);
                }
            }
        }
        //启动下载
        if(files==null || files.size()==0){
            downLoadCallBack.toPlay(terminalId, true);
            startPlayFileNum = -9999;
            clearTemp();
            return;
        }
        for (int i = 0; i < downSynNum; i++)
            taskExecutor.execute(new DownloadThreadTask());
    }

    class DownloadThreadTask implements Runnable {

        public void run() {
            while (taskisRun) {
                _WaitDownModel wdm = null;
                RandomAccessFile acf = null;
                Response response = null;
                try {
                    wdm = getWaitDownItem();
                    if (wdm == null) return;
                    File sf = new File(sourceDir + "/" + wdm.sourceId);
                    if (sf.exists()) {
                        sucessDownloadFile(wdm);
                        continue;
                    }
                    if(wdm.isOtherDownloading){
                        Thread.sleep(2000);
                        resetWaitDownItem(wdm);
                        continue;
                    }
                    long offset = 0;
                    File f = new File(tempDir + "/" + wdm.sourceId);
                    acf = new RandomAccessFile(f, "rwd");
                    if (f.exists()) {
                        offset = f.length();
                        wdm.downLoadSize = offset;
                        acf.seek(offset);
                    }
                    long fsize = getRemoteFileSize(wdm.sourceUrl);
                    if (fsize < 0) { //连接异常
                        resetWaitDownItem(wdm);
                        continue;
                    }
                    if (fsize == 0) {
                        sucessDownloadFile(wdm);
                        continue;//404
                    }
                    wdm.sourceSize=fsize;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(wdm.sourceUrl).
                            addHeader("Range", "bytes=" + offset + "-" + fsize).build();
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        acf.close();
                        response.close();
                        resetWaitDownItem(wdm);
                        continue;
                    }
                    InputStream ins = response.body().byteStream();
                    int len = 0;
                    byte[] buf = new byte[10240];
                    while ((len = ins.read(buf)) != -1) {
                        if (!taskisRun) return;
                        acf.write(buf, 0, len);
                        wdm.downLoadSize += len;
                    }
                    acf.close();
                    response.close();
                    f.renameTo(sf);
                    sucessDownloadFile(wdm);
                } catch (Exception e) {
                    try {
                        if (wdm != null) resetWaitDownItem(wdm);
                        if (acf != null) acf.close();
                        if (response != null) response.close();
                    } catch (Exception e1) {
                    }
                    //记录日志
                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}