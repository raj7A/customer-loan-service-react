package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import reactor.core.publisher.Mono;

public class CreateHomeLoanUseCaseImpl extends CreateLoanUseCaseAbstract {

    private final CustomerFraudCheckGateway customerFraudCheckGateway;

    public CreateHomeLoanUseCaseImpl(CustomerFraudCheckGateway customerFraudCheckService, LoanGateway loanGateway) {
        super(loanGateway);
        this.customerFraudCheckGateway = customerFraudCheckService;
    }

    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        return Mono.just(loanRequest)
                .flatMap(request -> doFraudCheck(request.customerId()))
                .filter(isFraud -> !isFraud)
                .flatMap(isFraud -> doCreateLoan(loanRequest))
                .flatMap(this::saveLoan);
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
