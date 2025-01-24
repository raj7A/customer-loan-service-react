package com.cc.customer.loan.service.usecases.createloanusecase;

import com.cc.customer.loan.service.entities.enums.LoanType;

public interface CreateLoanUseCaseFactory {
  CreateLoanUseCase getCreateLoanUseCase(LoanType loanType);
}
