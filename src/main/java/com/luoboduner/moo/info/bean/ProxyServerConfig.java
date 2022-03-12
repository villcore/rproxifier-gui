package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProxyServerConfig {

    private String name;
    private String addr;
    private short port;
    private boolean available;

    public static ProxyServerConfig create(String name, String addr, short port, boolean available) {
        ProxyServerConfig proxyServerConfig = new ProxyServerConfig();
        proxyServerConfig.name = name;
        proxyServerConfig.addr = addr;
        proxyServerConfig.port = port;
        proxyServerConfig.available = available;
        return proxyServerConfig;
    }
}
