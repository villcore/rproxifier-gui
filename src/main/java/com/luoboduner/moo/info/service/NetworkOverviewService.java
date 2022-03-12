package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.NetworkInterface;
import com.luoboduner.moo.info.bean.NetworkOverview;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class NetworkOverviewService {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkOverviewService.class);
    private static final NetworkOverviewService INSTANCE = new NetworkOverviewService();

    public static NetworkOverviewService getInstance() {
        return INSTANCE;
    }

    public Optional<NetworkOverview> getNetworkOverview() {
        return HttpClientUtil.get(
                Env.getRproxiferServerAddr() + "/overview/network",
                new TypeReference<NetworkOverview>() {}
        );
    }

    public boolean startNetwork(NetworkInterface networkInterface) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/net/start_net",
                networkInterface,
                new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public boolean stopNetwork(NetworkInterface networkInterface) {
        Optional<Boolean> result =  HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/net/stop_net",
                networkInterface,
                new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }
}
