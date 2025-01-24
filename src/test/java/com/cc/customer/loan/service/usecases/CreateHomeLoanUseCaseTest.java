package com.cc.customer.loan.service.usecases;

import com.cc.customer.loan.service.entities.HomeLoan;
import com.cc.customer.loan.service.entities.enums.LoanType;
import com.cc.customer.loan.service.usecases.createloanusecase.*;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CreateHomeLoanUseCaseTest {

    @Mock
    private CustomerFraudCheckGateway customerFraudCheckGateway;
    @Mock
    private LoanGateway loanGateway;
    private CreateLoanUseCase createLoanUseCase;

    @BeforeEach
    void setUp() {
        HashMap<String, String> carLoanProperties = new HashMap<>();
        carLoanProperties.put("isEnabled", Boolean.TRUE.toString());
        createLoanUseCase = new CreateHomeLoanUseCaseImpl(customerFraudCheckGateway, loanGateway);
    }

    @Test
    public void Home_loan_is_not_created_when_loan_is_not_saved_in_data_store() {
        var loanRequest = createLoanRequest(LoanType.CAR);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.FALSE)));
        Mockito.when(loanGateway.saveLoan(any())).thenThrow(new LoanSaveException("Error occurred while saving the loan in data store"));
        Mockito.when(loanGateway.findByCustomerId(any())).thenReturn(Mono.just(Boolean.FALSE));

        StepVerifier.create(createLoanUseCase.createLoan(loanRequest))
                .expectError(LoanSaveException.class)
                .verify();
    }

    @Test
    public void Home_loan_is_created_when_customer_passes_fraud_check() {
        var loanRequest = createLoanRequest(LoanType.HOME);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.FALSE)));
        Mockito.when(loanGateway.saveLoan(any())).thenReturn(Mono.just(1l));
        Mockito.when(loanGateway.findByCustomerId(any())).thenReturn(Mono.just(Boolean.FALSE));

        var homeLoan = createLoanUseCase.createLoan(loanRequest);

        StepVerifier.create(homeLoan)
                .expectNextMatches(loan -> loan instanceof HomeLoan)
                .verifyComplete();
    }

    private LoanRequest createLoanRequest(LoanType loanType) {
        return new LoanRequest("123", 12, 1000000.00, 0.00, loanType);
    }


}
