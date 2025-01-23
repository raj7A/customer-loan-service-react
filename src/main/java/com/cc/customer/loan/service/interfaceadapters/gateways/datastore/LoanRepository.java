package com.cc.customer.loan.service.interfaceadapters.gateways.datastore;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanRepository extends ReactiveCrudRepository<LoanDocument, String> {
}
