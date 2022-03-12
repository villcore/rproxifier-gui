package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessRegexRouteRule {
    public String process_path;
    public RegexRouteRule route_rule;
}
