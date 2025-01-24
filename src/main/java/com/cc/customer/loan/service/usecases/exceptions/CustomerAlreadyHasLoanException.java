package com.cc.customer.loan.service.usecases.exceptions;

public class CustomerAlreadyHasLoanException extends RuntimeException {
    public CustomerAlreadyHasLoanException(String message) {
        super(message);
    }
}
