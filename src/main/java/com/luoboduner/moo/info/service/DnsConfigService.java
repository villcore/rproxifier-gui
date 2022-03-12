package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.AllDnsHostConfig;
import com.luoboduner.moo.info.bean.DnsHost;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;

import java.util.Optional;

public class DnsConfigService {

    public static DnsConfigService INSTANCE = new DnsConfigService();

    public static DnsConfigService getInstance() {
        return INSTANCE;
    }

    public Optional<AllDnsHostConfig> getAllDnsConfig(String dnsQueryString) {
        return HttpClientUtil.get(Env.getRproxiferServerAddr() + "/dns/get_dns_config_list?dns_query=" + dnsQueryString,
                new TypeReference<AllDnsHostConfig>() {
                }
        );
    }

    public boolean saveDnsHostConfig(DnsHost dnsHost) {
        Optional<Boolean> saveResult = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/dns/set_dns_config",
                dnsHost,
                new TypeReference<Boolean>() {}
        );
        return saveResult.orElse(false);
    }
}
