package com.cc.customer.loan.service.interfaceadapters.controllers;

import com.cc.customer.loan.service.usecases.createloanusecase.CreateLoanUseCaseFactory;
import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.createloanusecase.LoanRequest;
import com.cc.customer.loan.service.usecases.properties.UseCaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.cc.customer.loan.service.interfaceadapters.controllers.CustomerLoanController.LoanRequestResponseMapper.LOAN_REQUEST_MAPPER;

@RestController
@Slf4j
public class CustomerLoanController {
    private final CreateLoanUseCaseFactory createLoanUseCaseFactory;
    private final UseCaseProperties useCaseProperties;

    public CustomerLoanController(CreateLoanUseCaseFactory createLoanUseCaseFactory, UseCaseProperties useCaseProperties) {
        this.createLoanUseCaseFactory = createLoanUseCaseFactory;
        this.useCaseProperties = useCaseProperties;
    }

    @PostMapping("/loan")
    public Mono<CreateLoanResponse> createLoan(@RequestBody CreateLoanRequest createLoanRequest) {
        return Mono.just(createLoanRequest)
                .doOnNext(request -> log.info("Received request to create loan: {}", request))
                .map(request -> createLoanUseCaseFactory.getCreateLoanUseCase(request.loanType()))
                .flatMap(createLoanUseCase -> createLoanUseCase.createLoan(LOAN_REQUEST_MAPPER.loanCreateRequestToUseCaseLoanRequest(createLoanRequest)))
                .map(LOAN_REQUEST_MAPPER::loanEntityToLoanResponse)
                .doOnNext(response -> log.info("Loan created: {}", response));
    }

    @Mapper
    public interface LoanRequestResponseMapper {
        LoanRequestResponseMapper LOAN_REQUEST_MAPPER = Mappers.getMapper(LoanRequestResponseMapper.class);

        LoanRequest loanCreateRequestToUseCaseLoanRequest(CreateLoanRequest createLoanRequest);

        CreateLoanResponse loanEntityToLoanResponse(Loan loan);
    }
}
