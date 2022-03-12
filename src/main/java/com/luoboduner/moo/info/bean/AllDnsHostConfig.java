package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AllDnsHostConfig {

    public String local_dns_server;
    public String gateway_server;
    public List<DnsHost> all_dns_config;
}
