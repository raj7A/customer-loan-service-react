package com.cc.customer.loan.service.interfaceadapters.gateways.datastore;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanRepository extends ReactiveCrudRepository<LoanDocument, Long> {
    Mono<LoanDocument> findByCustomerId(String customerId);
}
