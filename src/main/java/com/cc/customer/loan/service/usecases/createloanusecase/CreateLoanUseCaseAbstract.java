package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.entities.LoanFactory;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import reactor.core.publisher.Mono;

public abstract class CreateLoanUseCaseAbstract implements CreateLoanUseCase {

    private final LoanGateway loanGateway;

    protected CreateLoanUseCaseAbstract(LoanGateway loanGateway) {
        this.loanGateway = loanGateway;
    }

    Mono<Loan> doCreateLoan(LoanRequest loanRequest) {
        return Mono.just(LoanFactory.createLoan(loanRequest.loanType(), loanRequest.customerId(), loanRequest.period(),
                loanRequest.principle(), loanRequest.initialDiscountAmount()));
    }

    Mono<Loan> saveLoan(Loan loan) throws LoanSaveException {
        return loanGateway.saveLoan(loan)
                .flatMap(loanNumber -> {
                    loan.setLoanNumber(loanNumber);
                    return Mono.just(loan);
                });
    }

}
