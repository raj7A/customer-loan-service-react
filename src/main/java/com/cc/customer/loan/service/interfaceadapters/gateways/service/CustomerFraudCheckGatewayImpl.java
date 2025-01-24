package com.cc.customer.loan.service.interfaceadapters.gateways.service;

import com.cc.customer.loan.service.usecases.createloanusecase.CustomerFraudCheckGateway;
import com.cc.customer.loan.service.usecases.createloanusecase.FraudCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.cc.customer.loan.service.interfaceadapters.gateways.service.CustomerFraudCheckGatewayImpl.FraudCheckResponseMapper.FRAUD_CHECK_RESPONSE_MAPPER;

@Slf4j
public class CustomerFraudCheckGatewayImpl implements CustomerFraudCheckGateway {

    private final String resourceUrl = "http://localhost:8040/fraudCheck/{customerId}";
    private final WebClient webClient;

    public CustomerFraudCheckGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<FraudCheckResponse> doCustomerFraudCheck(String customerId) {
        log.info("Invoking fraudCheck for customer : {}", customerId);
        return webClient
                .get()
                .uri(resourceUrl, customerId)
                .retrieve()
                .bodyToMono(FraudCheckServiceResponse.class)
                .doOnNext(fraudCheckServiceResponse -> log.info("FraudCheck service Response received : {}", fraudCheckServiceResponse))
                .doOnError(throwable -> {
                    log.error("Error occurred while doing fraud check", throwable);
                    throw new RuntimeException("Downstream down", throwable);
                })
                .map(this::toLoanUseCaseResponse);
    }

    private FraudCheckResponse toLoanUseCaseResponse(FraudCheckServiceResponse fraudCheckServiceResponse) {
        return FRAUD_CHECK_RESPONSE_MAPPER.fraudCheckServiceResponseToFraudCheckResponse(fraudCheckServiceResponse);
    }

    @Mapper
    public interface FraudCheckResponseMapper {
        FraudCheckResponseMapper FRAUD_CHECK_RESPONSE_MAPPER = Mappers.getMapper(FraudCheckResponseMapper.class);

        FraudCheckResponse fraudCheckServiceResponseToFraudCheckResponse(FraudCheckServiceResponse fraudCheckServiceResponse);
    }
}
