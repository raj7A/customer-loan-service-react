package com.cc.customer.loan.service.interfaceadapters.gateways.datastore;

import com.cc.customer.loan.service.entities.enums.LoanStatus;
import com.cc.customer.loan.service.entities.enums.LoanType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "Loan")
class LoanDocument {
    @Id
    private Long loanNumber;
    private String customerId;
    private Integer period;
    private Double rate;
    private Double principle;
    private Double outStandingLoanAmount;
    private LoanType loanType;
    private LocalDate startDate;
    private LoanStatus loanStatus;
    private Double initialDiscountAmount;
    private final String loanCreatedSystem = "customer-loan";
}
