package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.ProcessRegexRouteRule;
import com.luoboduner.moo.info.bean.ProxyServerConfig;
import com.luoboduner.moo.info.bean.RegexRouteRule;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RouteRuleService {

    private static final Logger LOG = LoggerFactory.getLogger(RouteRuleService.class);
    private static final RouteRuleService INSTANCE = new RouteRuleService();

    public static RouteRuleService getInstance() {
        return INSTANCE;
    }

    public List<RegexRouteRule> getAllGlobalRouteRule() {
        Optional<List<RegexRouteRule>> result = HttpClientUtil.get(
                Env.getRproxiferServerAddr() + "/route/get_global_route",
                new TypeReference<List<RegexRouteRule>>() {}
        );
        return result.orElse(Collections.emptyList());
    }

    public boolean addGlobalRouteRule(RegexRouteRule regexRouteRule) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/add_global_rule",
                regexRouteRule, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public boolean saveGlobalRouteRule(List<RegexRouteRule> regexRouteRuleList) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/set_global_rule",
                regexRouteRuleList, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public boolean removeGlobalRouteRule(RegexRouteRule regexRouteRule) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/remove_global_rule",
                regexRouteRule, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public List<ProcessRegexRouteRule> getAllProcessRouteRule() {
        Optional<List<ProcessRegexRouteRule>> result = HttpClientUtil.get(
                Env.getRproxiferServerAddr() + "/route/get_all_process_route",
                new TypeReference<List<ProcessRegexRouteRule>>() {}
        );
        return result.orElse(Collections.emptyList());
    }

    public boolean addProcessRouteRule(ProcessRegexRouteRule processRegexRouteRule) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/add_process_rule",
                processRegexRouteRule, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public boolean saveProcessRouteRule(List<ProcessRegexRouteRule> processRegexRouteRuleList) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/set_process_rule",
                processRegexRouteRuleList, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }

    public boolean removeProcessRouteRule(ProcessRegexRouteRule processRegexRouteRule) {
        Optional<Boolean> result = HttpClientUtil.post(
                Env.getRproxiferServerAddr() + "/route/remove_process_rule",
                processRegexRouteRule, new TypeReference<Boolean>() {}
        );
        return result.orElse(false);
    }
}
