package com.cc.customer.loan.service.interfaceadapters.gateways.datastore;

import com.cc.customer.loan.service.entities.Loan;
import com.cc.customer.loan.service.usecases.createloanusecase.LoanGateway;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;

import static com.cc.customer.loan.service.interfaceadapters.gateways.datastore.LoanGatewayImpl.LoanMapper.LOAN_MAPPER;

public class LoanGatewayImpl implements LoanGateway {

    private final LoanRepository loanRepository;

    public LoanGatewayImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Mono<Long> saveLoan(Loan loan) {
        return Mono.just(loan)
                .map(this::toLoanRepositoryDocument)
                .flatMap(loanRepository::save)
                .map(LoanDocument::getLoanNumber);
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
