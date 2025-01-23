package com.cc.customer.loan.service.interfaceadapters.gateways.service;

import com.cc.customer.loan.service.usecases.createloanusecase.CustomerFraudCheckGateway;
import com.cc.customer.loan.service.usecases.createloanusecase.FraudCheckResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.cc.customer.loan.service.interfaceadapters.gateways.service.CustomerFraudCheckGatewayImpl.FraudCheckResponseMapper.FRAUD_CHECK_RESPONSE_MAPPER;

public class CustomerFraudCheckGatewayImpl implements CustomerFraudCheckGateway {

    private final String resourceUrl = "http://localhost:8040/fraudCheck/{customerId}";
    private final WebClient webClient;

    public CustomerFraudCheckGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<FraudCheckResponse> doCustomerFraudCheck(String customerId) {

        return webClient
                .get()
                .uri(resourceUrl, customerId)
                .retrieve()
                .bodyToMono(FraudCheckServiceResponse.class)
                .onErrorMap(throwable -> new RuntimeException("Downstream down", throwable))
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
