package com.cc.customer.loan.service.interfaceadapters.controllers;

import com.cc.customer.loan.service.entities.enums.LoanStatus;
import com.cc.customer.loan.service.entities.enums.LoanType;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static com.cc.customer.loan.service.drivers.InterfaceAdapterControllerConfigs.TRACE_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerLoanControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static WireMockServer wireMockServer = new WireMockServer(8040);

    @BeforeAll
    static void setUp() {
        wireMockServer.start();
        configureFor("localhost", 8040);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void Customer_creates_the_home_loan_for_12_months_period_that_is_not_activated() {
//        BlockHound.install();
        var createLoanRequest = new CreateLoanRequest("12345", 12, 10000.00, 0.00, LoanType.HOME, "test system", Boolean.TRUE);
        stubFor(WireMock.get(urlEqualTo("/fraudCheck/12345"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"customerId\":\"12345\",\"isFraud\":\"false\",\"trustScore\":\"10\"}")));

        CreateLoanResponse createLoanResponse =
                webTestClient
                        .post()
                        .uri("/loan")
                        .header(TRACE_ID, UUID.randomUUID().toString())
                        .bodyValue(createLoanRequest)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(CreateLoanResponse.class)
                        .returnResult()
                        .getResponseBody();

        Assertions.assertNotNull(createLoanResponse);
        Assertions.assertEquals(LoanStatus.CREATED, createLoanResponse.loanStatus());
    }

    @Test
    void Customer_creates_the_car_loan_for_12_months_period_that_is_activated() {
//        BlockHound.install();
        var createLoanRequest = new CreateLoanRequest("12345", 12, 10000.00, 0.00, LoanType.CAR, "test system", Boolean.TRUE);
        stubFor(WireMock.get(urlEqualTo("/fraudCheck/12345"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"customerId\":\"12345\",\"isFraud\":\"false\",\"trustScore\":\"10\"}")));

        CreateLoanResponse createLoanResponse =
                webTestClient
                        .post()
                        .uri("/loan")
                        .header(TRACE_ID, UUID.randomUUID().toString())
                        .bodyValue(createLoanRequest)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(CreateLoanResponse.class)
                        .returnResult()
                        .getResponseBody();

        Assertions.assertNotNull(createLoanResponse);
        Assertions.assertEquals(LoanStatus.ACTIVE, createLoanResponse.loanStatus());
    }

    @Test
    void Customer_is_not_able_to_create_home_loan_when_fails_the_fraud_check() {
//        BlockHound.install();
        var createLoanRequest = new CreateLoanRequest("12345", 12, 10000.00, 0.00, LoanType.HOME, "test system", Boolean.TRUE);
        stubFor(WireMock.get(urlEqualTo("/fraudCheck/12345"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"customerId\":\"12345\",\"isFraud\":\"true\",\"trustScore\":\"10\"}")));

        webTestClient
                .post()
                .uri("/loan")
                .header(TRACE_ID, UUID.randomUUID().toString())
                .bodyValue(createLoanRequest)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
