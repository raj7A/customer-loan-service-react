package com.cc.customer.loan.service.usecases.createloanusecase;

import reactor.core.publisher.Mono;

public interface CustomerFraudCheckGateway {
    Mono<FraudCheckResponse> doCustomerFraudCheck(String customerId);
}
