package io.complyt.testService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@RequiredArgsConstructor
public class EurekaConfig {
    @NonNull
    Environment environment;

    @Profile("production")
    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        String ip = null;

        try {
            ip = InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String port = environment.getProperty("server.port");
        config.setNonSecurePort(Integer.parseInt(port));
        config.setIpAddress(ip);
        config.setPreferIpAddress(true);

        return config;
    }
}