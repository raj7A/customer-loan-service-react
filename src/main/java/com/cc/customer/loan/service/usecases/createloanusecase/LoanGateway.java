package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import reactor.core.publisher.Mono;

public interface LoanGateway {
    Mono<Long> saveLoan(Loan loan);
    Mono<Boolean> findByCustomerId(String customerId);
}
