package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.entities.LoanFactory;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import reactor.core.publisher.Mono;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CreateLoanUseCaseImpl implements CreateLoanUseCase {

    private final CustomerFraudCheckGateway customerFraudCheckGateway;
    private final LoanGateway loanGateway;

    private static final Logger logger = LogManager.getLogManager().getLogger(CreateLoanUseCaseImpl.class.getName());

    public CreateLoanUseCaseImpl(CustomerFraudCheckGateway customerFraudCheckService, LoanGateway loanGateway) {
        this.customerFraudCheckGateway = customerFraudCheckService;
        this.loanGateway = loanGateway;
    }

    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        return Mono.just(loanRequest)
                .flatMap(request -> doFraudCheck(request.customerId()))
                .filter(isFraud -> !isFraud)
                .flatMap(isFraud -> doCreateLoan(loanRequest))
                .flatMap(this::saveLoan);
    }

    private Mono<Loan> doCreateLoan(LoanRequest loanRequest) {
        return Mono.just(LoanFactory.createLoan(loanRequest.loanType(), loanRequest.customerId(), loanRequest.period(),
                loanRequest.principle(), loanRequest.initialDiscountAmount()));
    }

    private Mono<Loan> saveLoan(Loan loan) throws LoanSaveException {
        return loanGateway.saveLoan(loan)
                .flatMap(loanNumber -> {
                    loan.setLoanNumber(loanNumber);
                    return Mono.just(loan);
                });
    }

    private Mono<Boolean> doFraudCheck(String customerId) throws CustomerFraudException {
        return customerFraudCheckGateway.doCustomerFraudCheck(customerId)
                .flatMap(fraudCheckResponse -> {
                    if (fraudCheckResponse.isFraud()) {
                        throw new CustomerFraudException("Customer failed the fraud check");
                    }
                    return Mono.just(Boolean.FALSE);
                });
    }

}
