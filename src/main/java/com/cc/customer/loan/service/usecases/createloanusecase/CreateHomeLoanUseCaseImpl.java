package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CreateHomeLoanUseCaseImpl extends CreateLoanUseCaseAbstract {

    private final CustomerFraudCheckGateway customerFraudCheckGateway;

    public CreateHomeLoanUseCaseImpl(CustomerFraudCheckGateway customerFraudCheckService, LoanGateway loanGateway) {
        super(loanGateway);
        this.customerFraudCheckGateway = customerFraudCheckService;
    }

    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        return customerFraudCheckGateway.doCustomerFraudCheck(loanRequest.customerId())
                .flatMap(fraudCheckResponse -> {
                    if (fraudCheckResponse.isFraud()) {
                        log.error("Customer with id {} failed the fraud check", loanRequest.customerId());
                        return Mono.error(new CustomerFraudException("Customer failed the fraud check"));
                    }
                    return Mono.just(fraudCheckResponse);
                })
                .flatMap(fraudCheckResponse -> doCreateLoan(loanRequest))
                .flatMap(this::saveLoan);
    }

}
