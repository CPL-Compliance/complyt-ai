//package com.complyt.config.client_wrapper_config;
//
//import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
//import com.complyt.config.WebClientWrapperProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebhookWebClientWrapperConfig {
//
//    @Bean("webhookClientWrapperProperties")
//    public WebhookWebClientWrapper webhookWebClientWrapperProperties(@Autowired WebClient webhookWebClient,
//                                                                     @Autowired WebClientWrapperProperties webhookWebClientWrapperProperties) {
//        return new WebhookWebClientWrapper(
//                webhookWebClient,
//                webhookWebClientWrapperProperties.getScheme(),
//                webhookWebClientWrapperProperties.getHost(),
//                webhookWebClientWrapperProperties.getPath());
//    }
//
//}