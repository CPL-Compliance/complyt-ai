package io.complyt.namingserver.config;

import com.netflix.appinfo.AmazonInfo;
import io.complyt.namingserver.annotations.Generated;
import lombok.NonNull;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Generated
@Configuration
public class EurekaConfig {
    @NonNull
    Environment environment;

    @Bean
    @Profile("production, az-1a, az-1b")
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) throws UnknownHostException {
        EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean(inetUtils);
        AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
        bean.setDataCenterInfo(info);

        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = environment.getProperty("server.port");
        bean.setNonSecurePort(Integer.parseInt(port));
        bean.setIpAddress(ip);
        bean.setPreferIpAddress(true);

        return bean;
    }
}
