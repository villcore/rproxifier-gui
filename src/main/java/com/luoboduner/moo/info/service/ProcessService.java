package com.luoboduner.moo.info.service;

import com.alibaba.fastjson.TypeReference;
import com.luoboduner.moo.info.bean.ProcessInfo;
import com.luoboduner.moo.info.util.Env;
import com.luoboduner.moo.info.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class ProcessService {

    public static ProcessService INSTANCE = new ProcessService();

    public static ProcessService getInstance() {
        return INSTANCE;
    }

    public Optional<List<ProcessInfo>> getAllProcessInfo(String processQueryStr) {
        processQueryStr = StringUtils.trimToEmpty(processQueryStr);
        return HttpClientUtil.get(Env.getRproxiferServerAddr() + "/process/get_all_process?process_query=" + processQueryStr,
                new TypeReference<List<ProcessInfo>>() {}
        );
    }
}
