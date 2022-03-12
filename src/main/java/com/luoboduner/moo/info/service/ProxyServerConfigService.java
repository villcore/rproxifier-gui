package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.ProxyServerConfig;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProxyServerConfigService {

    public static ProxyServerConfigService INSTANCE = new ProxyServerConfigService();

    public static ProxyServerConfigService getInstance() {
        return INSTANCE;
    }

    public List<ProxyServerConfig> getAllProxyServerConfig() {
        Optional<List<ProxyServerConfig>> proxyServerConfigList = HttpClientUtil.get(
                Env.getRproxiferServerAddr() + "/proxy_server/proxy_server_list",
                new TypeReference<List<ProxyServerConfig>>() {}
        );
        return proxyServerConfigList.orElse(Collections.emptyList());
    }

    public boolean saveProxyServerConfig(ProxyServerConfig proxyServerConfig) {
        Optional<Boolean> saveProxyServerConfigResult = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/proxy_server/add_proxy_server",
                proxyServerConfig, new TypeReference<Boolean>() {}
        );
        return saveProxyServerConfigResult.orElse(false);
    }

    public boolean removeProxyServerConfig(ProxyServerConfig proxyServerConfig) {
        Optional<Boolean> saveProxyServerConfigResult = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/proxy_server/remove_proxy_server",
                proxyServerConfig, new TypeReference<Boolean>() {}
        );
        return saveProxyServerConfigResult.orElse(false);
    }
}
