package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import reactor.core.publisher.Mono;

public interface CreateLoanUseCase {
    String createLoanUseCaseImpl = "CreateLoanUseCaseImpl";
    String createLoanUseCaseImplV1 = "CreateLoanUseCaseImplV1";

    Mono<Loan> createLoan(LoanRequest loanRequest);
}
