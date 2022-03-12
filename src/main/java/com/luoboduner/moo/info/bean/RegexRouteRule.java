package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.util.tuples.Pair;

import java.util.Map;

@Data
@NoArgsConstructor
public class RegexRouteRule {
    public String host_regex;
    public Object route_rule;

    public static Pair<RouteRuleTypeEnum, String> getRouteRuleType(Object rule) {
        if (rule instanceof Map) {
            Map ruleMap = (Map) rule;
            String typeCode = (String) ruleMap.keySet().toArray()[0];
            String typeConfig = (String) ruleMap.get(typeCode);
            RouteRuleTypeEnum typeEnum = RouteRuleTypeEnum.codeOf(typeCode);
            return new Pair<>(typeEnum, typeConfig);
        } else {
            // Direct
            RouteRuleTypeEnum typeEnum = RouteRuleTypeEnum.codeOf(rule.toString());
            return new Pair<>(typeEnum, "该类型无配置");
        }
    }
}
