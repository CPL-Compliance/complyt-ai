package com.complyt.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
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

    @Profile({"production", "demo", "test", "load-test"})
    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) throws UnknownHostException {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        String ip = InetAddress.getLocalHost().getHostAddress();

        String port = environment.getProperty("server.port");
        config.setNonSecurePort(Integer.parseInt(port));
        config.setIpAddress(ip);
        config.setPreferIpAddress(true);

        return config;
    }

//    @Profile({"integration-test"})
//    @Bean
//    public ServiceInstanceChooser serviceInstanceChooser(ReactiveDiscoveryClient discoveryClient) {
//        return new CustomServiceInstanceChooser(discoveryClient);
//    }
//
//    private record CustomServiceInstanceChooser(ReactiveDiscoveryClient discoveryClient) implements ServiceInstanceChooser {
//
//        @Override
//            public ServiceInstance choose(String serviceId) {
//                return discoveryClient.getInstances(serviceId)
//                        .map(serviceInstance ->
//                                !serviceInstance.getHost().equals("localhost") ? serviceInstance :
//                                        new DefaultServiceInstance(
//                                                serviceInstance.getInstanceId(),
//                                                serviceInstance.getServiceId(),
//                                                "host.docker.internal",
//                                                serviceInstance.getPort(),
//                                                serviceInstance.isSecure()))
//                        .next().block();
//            }
//
//            @Override
//            public <T> ServiceInstance choose(String serviceId, Request<T> request) {
//                return discoveryClient.getInstances(serviceId)
//                        .map(serviceInstance ->
//                                !serviceInstance.getHost().equals("localhost") ? serviceInstance :
//                                        new DefaultServiceInstance(
//                                                serviceInstance.getInstanceId(),
//                                                serviceInstance.getServiceId(),
//                                                "host.docker.internal",
//                                                serviceInstance.getPort(),
//                                                serviceInstance.isSecure()))
//                        .next().block();
//            }
//        }
}
