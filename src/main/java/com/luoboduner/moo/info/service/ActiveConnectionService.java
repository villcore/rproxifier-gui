package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.ActiveConnection;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ActiveConnectionService {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveConnectionService.class);
    private static final ActiveConnectionService INSTANCE = new ActiveConnectionService();

    public static ActiveConnectionService getInstance() {
        return INSTANCE;
    }

    public Optional<List<ActiveConnection>> getAllActiveConnection() {
        return HttpClientUtil.get(
                Env.getRproxiferServerAddr() + "/connection/active_connection_list",
                new TypeReference<List<ActiveConnection>>() {}
        );
    }
}
