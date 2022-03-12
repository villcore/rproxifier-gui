package com.luoboduner.moo.info.bean;

import org.apache.commons.lang3.StringUtils;

public enum RouteRuleTypeEnum {

    DIRECT("直接连接", "Direct"),
    REJECT("拒绝连接", "Reject"),
    PROXY("SOCKS_5代理", "Proxy"),
    PROBE("尝试直连，如果连接失败，使用SOCKS_5代理", "Probe");

    String title;
    String code;

    RouteRuleTypeEnum(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public static RouteRuleTypeEnum codeOf(String code) {
        for (RouteRuleTypeEnum type: values()) {
            if (StringUtils.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("code '"+ code +"' not find ");
    }

    public static RouteRuleTypeEnum titleOf(String title) {
        for (RouteRuleTypeEnum type: values()) {
            if (StringUtils.equals(type.getTitle(), title)) {
                return type;
            }
        }
        throw new IllegalArgumentException("title '"+ title +"' not find ");
    }
}
