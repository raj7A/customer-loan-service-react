package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import reactor.core.publisher.Mono;

public class CreateCarLoanUseCaseImpl extends CreateLoanUseCaseAbstract {

    public CreateCarLoanUseCaseImpl(LoanGateway loanGateway) {
        super(loanGateway);
    }

    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        return Mono.just(loanRequest)
                .flatMap(this::doCreateLoan)
                .flatMap(this::saveLoan);
    }
}
