package com.cc.customer.loan.service.drivers;

import com.cc.customer.loan.service.usecases.createloanusecase.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.cc.customer.loan.service.drivers.UseCaseConfigs.ConfigMapper.CONFIG_MAPPER;

@Configuration
public class UseCaseConfigs {

    @Bean(value = CreateLoanUseCase.CAR)
    public CreateLoanUseCase createCarLoanUseCase(LoanGateway loanGateway) {
        return new CreateCarLoanUseCaseImpl(loanGateway);
    }

    @Bean(value = CreateLoanUseCase.HOME)
    public CreateLoanUseCase createHomeLoanUseCase(CustomerFraudCheckGateway customerFraudCheckGateway, LoanGateway loanGateway) {
        return new CreateHomeLoanUseCaseImpl(customerFraudCheckGateway, loanGateway);
    }

    @Bean
    public com.cc.customer.loan.service.usecases.properties.UseCaseProperties createUseCaseProperties(UseCaseProperties useCaseProperties) {
        return CONFIG_MAPPER.toDomainConfig(useCaseProperties);
    }

    @Bean
    public ServiceLocatorFactoryBean getFulfilmentCheckProcessorFactoryBean() {
        ServiceLocatorFactoryBean serviceLocatorFactoryBean = new ServiceLocatorFactoryBean();
        serviceLocatorFactoryBean.setServiceLocatorInterface(CreateLoanUseCaseFactory.class);
        return serviceLocatorFactoryBean;
    }

    @Mapper
    public interface ConfigMapper {
        ConfigMapper CONFIG_MAPPER = Mappers.getMapper(ConfigMapper.class);

        com.cc.customer.loan.service.usecases.properties.UseCaseProperties toDomainConfig(UseCaseProperties useCaseProperties);

    }

}