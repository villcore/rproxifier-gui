package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NetworkOverview {
    private List<NetworkInterface> interface_list;
    private boolean network_state;
    private NetworkInterface bind_interface;
}
