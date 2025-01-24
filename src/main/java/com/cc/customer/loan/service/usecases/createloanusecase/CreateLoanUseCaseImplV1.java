package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.entities.LoanFactory;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CreateLoanUseCaseImplV1 implements CreateLoanUseCase {

    private final CustomerFraudCheckGateway customerFraudCheckGateway;
    private final LoanGateway loanGateway;

    private static final Logger logger = LogManager.getLogManager().getLogger(CreateLoanUseCaseImplV1.class.getName());

    public CreateLoanUseCaseImplV1(CustomerFraudCheckGateway customerFraudCheckService, LoanGateway loanGateway) {
        this.customerFraudCheckGateway = customerFraudCheckService;
        this.loanGateway = loanGateway;
    }


    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        return null;
    }
}
