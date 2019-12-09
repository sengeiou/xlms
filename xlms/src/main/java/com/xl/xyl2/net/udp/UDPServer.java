package com.xl.xyl2.net.udp;

import android.annotation.SuppressLint;

import com.gdswlw.library.toolkit.UIKit;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {

    static UDPServer _udpServer;

    public static synchronized void start(String serverHost, int serverProt, String dcode,
                                          int localPort, IcmdHandler cmdHander)  {
        try {
            if (_udpServer != null) _udpServer.disposable();
            _udpServer = new UDPServer(serverHost, serverProt, dcode, localPort, cmdHander);
            _udpServer.init();
        }
        catch (Exception e){

        }
    }
    public static void stop(){
        if (_udpServer != null) _udpServer.disposable();
    }

    public static UDPServer current() {
        return _udpServer;
    }

    public String getWapIp() {
        return wapIp;
    }

    public int getWapPort() {
        return wapPort;
    }

    public boolean IsLink() {
        return isLink;
    }

    private String dcode;// 设备命令通信标识
    private boolean isLink = false;
    private String wapIp; // 当前UDP跟服务器建立的通道的外网IP
    private int wapPort; // 当前UDP跟服务器建立的通道的外网端口号

    private int localProt = 0;// 客户端端口号
    private DatagramSocket socket = null;// 创建套接字

    private ListerTh listerTh = null;// 监听线程
    private SnyTh synTh = null;// 同步线程

    private Hashtable retrunValueCache = null;// 用于保存返回的值
    private Hashtable handleEndCache = null;// 用于保存执行的结果

    private String serverHost;
    private InetAddress serverAddr = null;// 服务器的地址
    private int serverPort;// 服务器端口号
    private DatagramPacket packet = null;// 发送的数据包

    private int ResponseOutTime = 12000;// 响应时间
    private int RequestCacheOutTime = 30000;// 请求时间

    private ExecutorService receiveHanderThreadPool; // 接收处理线程池
    private IcmdHandler cmdHander = null;

    public UDPServer(String serverHost, int serverProt, String dcode,
                     int localPort, IcmdHandler cmdHander) {
        this.serverHost = serverHost;
        this.serverPort = serverProt;
        this.dcode = dcode;
        this.localProt = localPort;
        this.cmdHander = cmdHander;
    }

    // 初始化UDP
    public void init() throws SocketException, IOException {
        this.serverAddr = InetAddress.getByName(serverHost);
        this.socket = new DatagramSocket();
        this.retrunValueCache = new Hashtable();
        this.handleEndCache = new Hashtable();
        receiveHanderThreadPool = Executors.newCachedThreadPool();

        if (listerTh == null) {
            listerTh = new ListerTh();
            listerTh.start();
        }
        if (synTh == null) {
            synTh = new SnyTh();
            synTh.start();
        }
    }

    // 释放
    public void disposable() {
        // 杀死线程 关闭UDP
        if (listerTh != null) {
            listerTh.isRun = false;
            listerTh = null;
        }
        if (synTh != null) {
            synTh.isRun = false;
            synTh = null;
        }
        if (socket != null) {
            socket.close();
            socket.disconnect();
            socket = null;
        }
    }

    // 发送字节数组
    private void send(byte[] msg)
            throws IOException {
        DatagramPacket packet = new DatagramPacket(msg, msg.length,
                serverAddr, serverPort);
        socket.send(packet);
    }

    // 向指定的IP端口号发送命令
    public void send(MsgModel msg)
            throws IOException {
        send((new Gson()).toJson(msg).getBytes("utf-8"));
    }

    public Object sendWaitReturn(String cmd, Object value) throws Exception {
        return sendWaitReturn(cmd, value, null);
    }

    public Object sendWaitReturn(String cmd, Object value, String to) throws Exception {
        String key = UUID.randomUUID().toString();// 生成唯一的标识
        MsgModel msg = new MsgModel();
        msg.f = dcode;// 设备通信标识
        msg.t = to;
        msg.c = cmd;
        msg.i = key;
        msg.v = value;
        int waitTime = 0;
        retrunValueCache.put(key, "!");
        while (true) {
            if (waitTime % 3000 == 0 || waitTime == 1000)
                send(msg);// 500毫秒继续发送一次
            Thread.sleep(100);
            waitTime += 100;
            if (!retrunValueCache.get(key).equals("!")) {
                Object end = retrunValueCache.get(key);
                retrunValueCache.remove(key);
                return end;
            }
            if (waitTime >= ResponseOutTime) {
                retrunValueCache.remove(key);
                throw new Exception("等待超时");
            }
        }
    }


    // 与服务器进行同步
    class SnyTh extends Thread {
        public boolean isRun = true;

        @Override
        public void run() {
            while (isRun) {
                try {
                    String end = sendWaitReturn("syn", null)
                            .toString();
                    String[] ends = end.split(":");
                    wapIp = ends[0];
                    wapPort = Integer.parseInt(ends[1]);
                    isLink = true;
                    Thread.sleep(30000);// 半分钟与服务器进行一次同步
                } catch (Exception e) {
                    isLink = false;
                    e.printStackTrace();
                }
            }
        }
    }

    // 接收处理单元
    class ReceiveHander implements Runnable {
        DatagramPacket packet;

        public ReceiveHander(DatagramPacket p) {
            packet = p;
        }

        @Override
        public void run() {
            try {
                byte[] backbuf = new byte[packet.getLength()];
                // 1、源数组 2、源数组要复制的起始位置 3、目的数组 4、目的数组放置的起始位置 5、复制的长度
                System.arraycopy(packet.getData(), 0, backbuf, 0,
                        backbuf.length);
                InetAddress visitip = packet.getAddress();// 获取服务器的IP地址
                int visiport = packet.getPort();// 获取服务器的端口号
                MsgModel mm = null;

                String json = new String(backbuf, 0, backbuf.length,
                        "UTF-8").trim();// 接收服务器返回来的数据，并进行UTF-8编码
                mm = (new Gson()).fromJson(json, MsgModel.class); // 将接收到的信息转换为通信模型

                MsgModel rm = new MsgModel(); // 信息模型 用于向服务器发送数据包
                rm.f = dcode;// 发送方的ID (设备命令通信标识)
                rm.t = mm.f;// 接收方的ID (客户端端通信标识)
                rm.i = mm.i;// 返回标识
                // 从缓存查找对应结果
                if (mm.c != null && !mm.c.isEmpty()) {
                    // 处理命令
                    HandleCacheModle hcm = null;
                    if (mm.i != null && !mm.i.isEmpty()) {
                        // 从缓存区中找是否有执行结果
                        Object end = handleEndCache.get(mm.i);
                        if (end != null) {
                            hcm = (HandleCacheModle) end;
                            hcm.setRunTime(System.currentTimeMillis());
                            if ((hcm.getValue() instanceof String)
                                    && hcm.getValue().toString()
                                    .equals("!"))
                                return;
                            rm.v = ((HandleCacheModle) end).getValue();// 如果找到了将执行结果发送给命令发送方
                            UDPServer.this.send(rm);
                            return;
                        }
                        hcm = new HandleCacheModle(System.currentTimeMillis(),
                                "!");
                        handleEndCache.put(mm.i, hcm);
                        // 删除缓冲区过时缓冲结果,从结果缓存区查找是否有对应的执行结果,没有则执行C对应的命令，然后将执行结果加入结果缓存区，向命令发送方发送执行结果
                        List<Object> outkeys = new ArrayList<Object>();
                        // 遍历hashtable
                        for (Iterator it = handleEndCache.keySet().iterator(); it
                                .hasNext(); ) {
                            // 从ht中取
                            Object key = it.next();
                            long t = System.currentTimeMillis()
                                    - ((HandleCacheModle) handleEndCache
                                    .get(key)).getRunTime();
                            if (t >= RequestCacheOutTime) { // 判断是否超时,超过时间则清除缓存
                                outkeys.add(key); // 由对象value中的time得到key？
                            }
                        }
                        // 遍历list集合
                        for (Object k : outkeys) {
                            handleEndCache.remove(k);// 删除过时缓存结果
                        }
                    }
                    if (cmdHander == null)
                        return;
                    UIKit.dLog("ccccccccc");
                    rm.v = cmdHander.Execute(mm.c, mm.v, UDPServer.this);
                    if (hcm != null) {
                        // 将执行结果加入结果缓存区
                        UDPServer.this.send(rm);
                        hcm.setValue(rm.v);
                    }
                    return;
                }
                // mm.C为空,判断R是否为空，R为空则跳出，R不为空则检测返回值中是否存在该项，不存在跳出，存在则将V赋值给R
                if (mm.i != null && !mm.i.isEmpty()) {
                    // C为空，R不为空,检测返回值是否存在指定缓存项,将V赋值给R指定缓存项
                    if (retrunValueCache.get(mm.i) != null) {
                        retrunValueCache.put(mm.i, mm.v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 监听线程
    class ListerTh extends Thread {
        public boolean isRun = true;

        public ListerTh() {
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            while (isRun) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[2000],
                            2000);
                    socket.receive(packet);// 接收服务器发来的数据
                    receiveHanderThreadPool.execute(new ReceiveHander(packet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
