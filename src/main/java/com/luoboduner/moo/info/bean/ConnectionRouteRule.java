package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.util.tuples.Pair;

import java.util.Map;

@Data
@NoArgsConstructor
public class ConnectionRouteRule {
    public Boolean hit_global_rule;
    public Boolean hit_process_rule;
    public Boolean need_proxy;
    public String host_regex;
    public Object route_rule;
}
