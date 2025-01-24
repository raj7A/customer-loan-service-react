package com.cc.customer.loan.service.usecases;

import com.cc.customer.loan.service.entities.CarLoan;
import com.cc.customer.loan.service.entities.HomeLoan;
import com.cc.customer.loan.service.entities.enums.LoanType;
import com.cc.customer.loan.service.usecases.createloanusecase.*;
import com.cc.customer.loan.service.usecases.exceptions.CustomerFraudException;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import com.cc.customer.loan.service.usecases.properties.UseCaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CreateLoanUseCaseTest {

    @Mock
    private CustomerFraudCheckGateway customerFraudCheckGateway;
    @Mock
    private LoanGateway loanGateway;
    private CreateLoanUseCase createLoanUseCase;
    private UseCaseProperties useCaseProperties = new UseCaseProperties(false, new HashMap<>());

    @BeforeEach
    void setUp() {
        HashMap<String, String> carLoanProperties = new HashMap<>();
        carLoanProperties.put("isEnabled", Boolean.TRUE.toString());
        useCaseProperties.feature().put("carLoan", carLoanProperties);
        createLoanUseCase = new CreateLoanUseCaseImpl(customerFraudCheckGateway, loanGateway);
    }

    @Test
    public void Car_loan_is_created_when_customer_passes_fraud_check() {
        var loanRequest = createLoanRequest(LoanType.CAR);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.FALSE)));
        Mockito.when(loanGateway.saveLoan(any())).thenReturn(Mono.just(1l));

        var carLoan = createLoanUseCase.createLoan(loanRequest);

        StepVerifier.create(carLoan)
                .assertNext(loan -> {
                    assertNotNull(loan);
                    assertInstanceOf(CarLoan.class, loan);
                })
                .verifyComplete();
    }

    @Test
    public void Home_loan_is_created_when_customer_passes_fraud_check() {
        var loanRequest = createLoanRequest(LoanType.HOUSING);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.FALSE)));
        Mockito.when(loanGateway.saveLoan(any())).thenReturn(Mono.just(1l));

        var homeLoan = createLoanUseCase.createLoan(loanRequest);

        StepVerifier.create(homeLoan)
                .assertNext(loan -> {
                    assertNotNull(loan);
                    assertInstanceOf(HomeLoan.class, loan);
                })
                .verifyComplete();
    }

    @Test
    public void Car_loan_is_not_created_when_customer_fails_fraud_check() {
        var loanRequest = createLoanRequest(LoanType.CAR);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.TRUE)));

        StepVerifier.create(createLoanUseCase.createLoan(loanRequest))
                .expectError(CustomerFraudException.class)
                .verify();
    }

    @Test
    public void Car_loan_is_not_created_when_loan_is_not_saved_in_data_store() {
        var loanRequest = createLoanRequest(LoanType.CAR);
        Mockito.when(customerFraudCheckGateway.doCustomerFraudCheck(any())).thenReturn(Mono.just(new FraudCheckResponse("123", Boolean.FALSE)));
        Mockito.when(loanGateway.saveLoan(any())).thenThrow(new LoanSaveException("Error occurred while saving the loan in data store"));

        StepVerifier.create(createLoanUseCase.createLoan(loanRequest))
                .expectError(LoanSaveException.class)
                .verify();
    }

    private LoanRequest createLoanRequest(LoanType loanType) {
        return new LoanRequest("123", 12, 1000000.00, 0.00, loanType);
    }

}
