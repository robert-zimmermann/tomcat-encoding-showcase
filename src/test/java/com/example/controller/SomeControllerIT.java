package com.example.controller;

import java.util.stream.Stream;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.example.service.RedirectUrlService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SomeControllerIT {
    @MockBean
    private RedirectUrlService redirectUrlService;

    @LocalServerPort
    private int port;

    private static Stream<Arguments> testUrls() {
        return Stream.of(
                arguments("http://dummy.url?non_acii=Ä",
                        "http://dummy.url?non_acii=Ä"),

                arguments("http://dummy.url?non_latin1_char=—",
                        "http://dummy.url?non_latin1_char=?")
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testUrls")
    void expectRedirectLocationExists(String redirectUrl, String expectedLocationHeader) {
        final var restTemplate = restTemplateWithDisabledRedirectHandling();
        when(redirectUrlService.getRedirectUrl()).thenReturn(redirectUrl);
        var httpHeaders = new HttpHeaders();

        final var responseEntity = restTemplate.exchange("http://localhost:" + port + "/hello",
                HttpMethod.GET, new HttpEntity<>(httpHeaders), ResponseEntity.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY);
        var location = responseEntity.getHeaders().get("Location");
        assertThat(location).as("Expected Location-Header not found").isNotNull();
        assertThat(location.get(0)).isEqualTo(expectedLocationHeader);
    }

    private RestTemplate restTemplateWithDisabledRedirectHandling() {
        final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient build = HttpClientBuilder.create()
                .disableRedirectHandling().build();
        httpRequestFactory.setHttpClient(build);
        return new RestTemplate(httpRequestFactory);
    }
}