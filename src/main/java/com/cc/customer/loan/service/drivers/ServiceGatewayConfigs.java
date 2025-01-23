package com.cc.customer.loan.service.drivers;

import com.cc.customer.loan.service.usecases.createloanusecase.CustomerFraudCheckGateway;
import com.cc.customer.loan.service.interfaceadapters.gateways.service.CustomerFraudCheckGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceGatewayConfigs {

    @Bean
    public CustomerFraudCheckGateway customerFraudCheckGatewayBean(WebClient webClient) {
        return new CustomerFraudCheckGatewayImpl(webClient);
    }

    @Bean
    public WebClient createWebClient() {
        return WebClient.builder().build();
    }
}
