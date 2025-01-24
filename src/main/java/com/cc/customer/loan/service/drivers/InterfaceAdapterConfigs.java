package com.cc.customer.loan.service.drivers;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class InterfaceAdapterConfigs {

    @Bean
    public WebFilter filterFunction() {
        return new filterFunction();
    }
}


class filterFunction implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getRequest().getHeaders().get("TRACE_ID").stream()
                .findFirst()
                .ifPresent(traceId -> MDC.put("traceId", traceId));
        return chain.filter(exchange);
    }
}
