package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.exceptions.CustomerAlreadyHasLoanException;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Slf4j
public class CreateHomeLoanUseCaseImpl extends CreateLoanUseCaseAbstract {

    private final CustomerFraudCheckGateway customerFraudCheckGateway;
    private final LoanGateway loanGateway;

    public CreateHomeLoanUseCaseImpl(CustomerFraudCheckGateway customerFraudCheckService, LoanGateway loanGateway) {
        super(loanGateway);
        this.customerFraudCheckGateway = customerFraudCheckService;
        this.loanGateway = loanGateway;
    }

    @Override
    public Mono<Loan> createLoan(LoanRequest loanRequest) {
        Mono<Boolean> doesCustomerAlreadyHasALoanMono = doesCustomerAlreadyHasALoan(loanRequest);
        Mono<FraudCheckResponse> fraudCheckResponseMono = customerFraudCheckGateway.doCustomerFraudCheck(loanRequest.customerId());
        return Mono.zip(doesCustomerAlreadyHasALoanMono, fraudCheckResponseMono)
                .flatMap(responseTuple -> isValidLoanRequestFromCustomer(loanRequest, responseTuple))
                .flatMap(isValidLoanRequestFromCustomer -> doCreateLoan(loanRequest))
                .flatMap(this::saveLoan)
                .subscribeOn(Schedulers.boundedElastic()); // this subscribeOn is to replicate the traceId not coming consistently in the logs due to asynch & mdc approach
    }

    private static Mono<Boolean> isValidLoanRequestFromCustomer(LoanRequest loanRequest, Tuple2<Boolean, FraudCheckResponse> responseTuple) {
        Boolean doesCustomerAlreadyHasALoan = responseTuple.getT1();
        FraudCheckResponse fraudCheckResponse = responseTuple.getT2();
        if (doesCustomerAlreadyHasALoan) {
            log.error("Customer with id {} already has a loan", loanRequest.customerId());
            return Mono.error(new CustomerAlreadyHasLoanException("Customer failed the fraud check"));
        }
        if (fraudCheckResponse.isFraud()) {
            log.error("Customer with id {} failed the fraud check", loanRequest.customerId());
            return Mono.error(new CustomerFraudException("Customer failed the fraud check"));
        }
        return Mono.just(Boolean.TRUE);
    }

    private Mono<Boolean> doesCustomerAlreadyHasALoan(LoanRequest loanRequest) throws LoanSaveException {
        return loanGateway.findByCustomerId(loanRequest.customerId());
    }

}
