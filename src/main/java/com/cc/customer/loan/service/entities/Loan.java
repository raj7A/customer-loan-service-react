package com.cc.customer.loan.service.entities;

import com.cc.customer.loan.service.entities.enums.LoanStatus;
import com.cc.customer.loan.service.entities.enums.LoanType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public abstract class Loan {
    private Long loanNumber;
    private final String customerId;
    private final Integer period;
    private final Double rate;
    private final Double principle;
    private Double outStandingLoanAmount;
    private final LoanType loanType;
    private final LocalDate startDate;
    private LoanStatus loanStatus;
    private final Double initialDiscountAmount;

    protected Loan(String customerId, LoanType loanType, Integer period, Double principle, Double initialDiscountAmount) {
        this.initialDiscountAmount = initialDiscountAmount;
        this.customerId = customerId;
        this.loanType = loanType;
        this.period = period;
        this.rate = getInterestRatePercentage();
        this.principle = principle;
        this.startDate = calculateStartDate();
        this.loanStatus = LoanStatus.CREATED;
        this.outStandingLoanAmount = calculateInitialOutStandingAmount();
    }

    private Double calculateInitialOutStandingAmount() {
        return principle - initialDiscountAmount;
    }

    public Double makePayment(Double amount) {
        outStandingLoanAmount = outStandingLoanAmount - amount;
        return outStandingLoanAmount;
    }

    public Double calculateMonthlyPaymentAmount() {
        return (principle * getInterestRatePercentage()) / 100;
    }

    protected LocalDate calculateStartDate() {
        return LocalDate.now();
    }

    protected void activateLoan() {
        loanStatus = LoanStatus.ACTIVE;
    }

    protected abstract Double getInterestRatePercentage();
}
