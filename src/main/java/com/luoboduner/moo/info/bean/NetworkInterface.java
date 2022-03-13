package com.luoboduner.moo.info.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Data
@NoArgsConstructor
public class NetworkInterface {
    private String interface_name;
    private String ip_addr;

    public static String format(NetworkInterface networkInterface) {
       return String.format("%s => %s", networkInterface.getInterface_name(), networkInterface.getIp_addr());
    }

    public static Optional<NetworkInterface> formFormat(String format) {
        if (StringUtils.isBlank(format)) {
            return Optional.empty();
        }

        String[] splits = StringUtils.split(format, "=>");
        if (splits.length != 2) {
            return Optional.empty();
        }

        String interfaceName = splits[0];
        String interfaceIpAddr = splits[1];
        if (StringUtils.isBlank(interfaceName) || StringUtils.isBlank(interfaceIpAddr)) {
            return Optional.empty();
        }

        NetworkInterface networkInterface = new NetworkInterface();
        networkInterface.interface_name = interfaceName.trim();
        networkInterface.ip_addr = interfaceIpAddr.trim();
        return Optional.of(networkInterface);
    }
}
