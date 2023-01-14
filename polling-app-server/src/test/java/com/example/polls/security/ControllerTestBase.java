package com.example.polls.security;

import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient(timeout = "36000")
class ControllerTestBase {
    @Autowired
    protected WebTestClient webTestClient;

    // @Value("${app.jwtSecret}")
    // private String jwtSecret;

    // @Value("${app.jwtExpirationMs}")
    // private int jwtExpirationMs;

    protected StatusAssertions getAdmins_WrongAuthHeader(String accessToken) {
        return webTestClient.get().uri("/admin")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",accessToken)
                .exchange()
                .expectStatus();
    }
}
