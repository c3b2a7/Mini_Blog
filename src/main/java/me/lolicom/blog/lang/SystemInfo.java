package me.lolicom.blog.lang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author lolicom
 */
@Functional
@Component
public class SystemInfo implements EnvironmentAware {

    private static Environment environment;
    private static String port;

    @Override
    public void setEnvironment(Environment environment) {
        SystemInfo.environment = environment;
    }

    @Autowired
    public void setPort(@Value("${server.port}") String port) {
        SystemInfo.port = port;
    }

    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }

    public String[] getDefaultProfiles() {
        return environment.getDefaultProfiles();
    }

    public boolean acceptsProfiles(Profiles profiles) {
        return environment.acceptsProfiles(profiles);
    }

    public boolean containsProperty(String key) {
        return environment.containsProperty(key);
    }

    @Nullable
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    @Nullable
    public <T> T getProperty(String key, Class<T> targetType) {
        return environment.getProperty(key, targetType);
    }

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return environment.getProperty(key, targetType, defaultValue);
    }

    public String getRequiredProperty(String key) throws IllegalStateException {
        return environment.getRequiredProperty(key);
    }

    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return environment.getRequiredProperty(key, targetType);
    }

    public String resolvePlaceholders(String text) {
        return environment.resolvePlaceholders(text);
    }

    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return environment.resolveRequiredPlaceholders(text);
    }

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
        if (port != null && !"80".equals(port)) {
            return ip + ":" + port;
        }
        return ip;
    }
}
