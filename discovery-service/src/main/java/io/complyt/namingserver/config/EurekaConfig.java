package io.complyt.namingserver.config;

import com.netflix.appinfo.AmazonInfo;
import io.complyt.namingserver.annotations.Generated;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Generated
@Configuration
public class EurekaConfig {
    @Bean
    @Profile({"production", "prod-az-1a", "prod-az-1b"})
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        EurekaInstanceConfigBean bean = new EurekaInstanceConfigBean(inetUtils);
        AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
        bean.setDataCenterInfo(info);

        return bean;
    }
}