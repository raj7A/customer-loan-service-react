package com.cc.customer.loan.service.interfaceadapters.gateways.datastore;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.createloanusecase.LoanGateway;
import com.cc.customer.loan.service.usecases.exceptions.LoanSaveException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.cc.customer.loan.service.interfaceadapters.gateways.datastore.LoanGatewayImpl.LoanMapper.LOAN_MAPPER;

@Slf4j
public class LoanGatewayImpl implements LoanGateway {

    private final LoanRepository loanRepository;

    public LoanGatewayImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Mono<Long> saveLoan(Loan loan) {
        return Mono.just(loan)
                .doOnNext(loanDetails -> log.info("Saving loan in data store : {}", loanDetails))
                .map(this::toLoanRepositoryDocument)
                .flatMap(loanRepository::save)
                .doOnError(throwable -> log.error("Error occurred while saving the loan in data store", throwable))
                .onErrorMap(throwable -> new LoanSaveException("Error occurred while saving the loan in data store"))
                .map(LoanDocument::getLoanNumber)
                .doOnNext(loanNumber -> log.info("Loan with id '{}' saved successfully in data store", loanNumber));
    }

    @Override
    public Mono<Boolean> findByCustomerId(String customerId) {
        return loanRepository.findByCustomerId(customerId)
                .map(Objects::nonNull)
                .doOnNext(loan -> log.info("Customer with id '{}' fetched the loan successfully from data store", customerId))
                .switchIfEmpty(Mono.just(false))
                .doOnNext(loan -> log.info("Customer with id '{}' does not have any loan in data store", customerId));
    }

    private LoanDocument toLoanRepositoryDocument(Loan loan) {
        return LOAN_MAPPER.loanDomainEntityToLoanDocument(loan);
    }

    @Mapper
    public interface LoanMapper {
        LoanMapper LOAN_MAPPER = Mappers.getMapper(LoanMapper.class);

        LoanDocument loanDomainEntityToLoanDocument(Loan loan);
    }
}
