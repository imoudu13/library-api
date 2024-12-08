package com.moducation.library.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityConfigTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void registerEndpoint_ShouldBeAccessibleWithoutAuthentication() {
        // Arrange
        String url = "http://localhost:" + port + "/api/users/register";

        // Act
        var response = restTemplate.postForEntity(url, null, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // Or CREATED if payload is valid
    }

    @Test
    public void loginEndpoint_ShouldBeAccessibleWithoutAuthentication() {
        // Arrange
        String url = "http://localhost:" + port + "/api/users/login";

        // Act
        var response = restTemplate.postForEntity(url, null, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // Replace based on expected behavior
    }
}