package com.xl.xyl2.net.udp;

public interface IcmdHandler {
    Object Execute(String c, Object v, UDPServer us);
}
