package com.cc.customer.loan.service.drivers;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Hooks;
import reactor.util.context.Context;

@Configuration
public class InterfaceAdapterControllerConfigs {

    public static final String TRACE_ID = "traceId";

    @Bean
    public WebFilter traceIdFilterFunction() {
        return (exchange, chain) -> {
            String traceId = exchange.getRequest()
                    .getHeaders()
                    .get(TRACE_ID)
                    .stream()
                    .findFirst()
                    .orElse("default-trace-id-from-config");
            return chain.filter(exchange).contextWrite(Context.of(TRACE_ID, traceId));
        };
    }

    @PostConstruct
    public void initTraceIdContextRegistry() {
        Hooks.enableAutomaticContextPropagation();
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(TRACE_ID,
                        () -> MDC.get(TRACE_ID),
                        traceId -> MDC.put(TRACE_ID, traceId),
                        () -> MDC.remove(TRACE_ID));
    }

    //@Bean
    // mdc based filter - not suitable for asynch workflow
    public WebFilter filterFunction() {
        return (exchange, chain) -> {
            exchange.getRequest().getHeaders().get(TRACE_ID).stream()
                    .findFirst()
                    .ifPresent(traceId -> MDC.put(TRACE_ID, traceId));
            return chain.filter(exchange);
        };
    }
}