package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActiveConnection {
    private Integer pid;
    private String process_name;
    private String process_execute_path;
    private Integer session_port;
    private String src_addr;
    private Integer src_port;
    private String dst_addr;
    private Integer dst_port;
    private ConnectionRouteRule route_rule;
    private Long tx;
    private Long rx;
    private Long latest_touch_timestamp;
    private Long pre_tx;
    private Long pre_rx;
    private Long pre_touch_timestamp;
    private Long start_timestamp;
}
