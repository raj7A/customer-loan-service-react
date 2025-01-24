package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import reactor.core.publisher.Mono;

public interface CreateLoanUseCase {
    String CAR = "CAR";
    String HOME = "HOME";

    Mono<Loan> createLoan(LoanRequest loanRequest);
}
