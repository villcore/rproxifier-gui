package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DnsHost {

    private String host;
    private List<String> related_process_vec;
    private boolean reverse_resolve;

    public static DnsHost create(String host, List<String> related_process_vec, boolean reverse) {
        DnsHost dnsHost = new DnsHost();
        dnsHost.host = host;
        dnsHost.related_process_vec = related_process_vec;
        dnsHost.reverse_resolve = reverse;
        return dnsHost;
    }
}
