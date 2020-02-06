package me.lolicom.blog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author lolicom
 */
@Component
public class SystemInfo implements EnvironmentAware {
    
    private Environment environment;
    
    @Value("${server.port}")
    private String port;
    
    public String getIp() {
        InetAddress localHost;
        try {
            localHost = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return localHost.getHostAddress();
    }
    
    public String getPort() {
        return Optional.ofNullable(port).orElseGet(() -> environment.getProperty("local.server.port"));
    }
    
    public String getServerAddress() {
        String ip = getIp();
        String port = getPort();
        if (port != null && !"80".equals(port))
            return ip + ":" + port;
        return ip;
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
