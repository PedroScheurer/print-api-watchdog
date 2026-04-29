package br.com.pedroscheurer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchDogApiClientTest {

    private HttpClient httpClientMock;
    private WatchdogApiClient apiClient;

    @BeforeEach
    void setup() {
        httpClientMock = mock(HttpClient.class);
        apiClient = new WatchdogApiClient("https://api.icfc.com.br", httpClientMock);
    }

    @Test
    void shouldReturnTokenWhenSuccessfullyLogin() throws Exception {
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.body()).thenReturn("{\"token\":\"abc123\"}");
        when(httpClientMock.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(responseMock);

        String token = apiClient.requestToken(
                "/paginacfc/auth/login",
                "{\"email\":\"robo@email.com\",\"password\":\"123\"}"
        );

        assertEquals("abc123", token);
    }

    @Test
    void shouldThrowExceptionWhenLoginFails() throws Exception {
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.statusCode()).thenReturn(401);
        when(responseMock.body()).thenReturn("{\"error\":\"Unauthorized\"}");
        when(httpClientMock.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(responseMock);

        assertThrows(IOException.class, () ->
                apiClient.requestToken(
                        "/paginacfc/auth/login",
                        "{\"email\":\"robo@email.com\",\"password\":\"errada\"}"
                )
        );
    }

    @Test
    void shouldReturnBodyWhenSuccessfullyGeneratePdf() throws Exception {
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.body()).thenReturn("{\"status\":\"ok\"}");
        when(httpClientMock.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(responseMock);

        String response = apiClient.verifyPdfImpression(
                "/icfc/parcela/gerarrecibo/6403614?tipo=0&numero-vias=2",
                "abc123"
        );

        assertEquals("{\"status\":\"ok\"}", response);
    }

    @Test
    void shouldThrowExceptionWhenPdfGenerationFails() throws Exception {
        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.statusCode()).thenReturn(500);
        when(responseMock.body()).thenReturn("{\"error\":\"Internal Server Error\"}");
        when(httpClientMock.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(responseMock);

        assertThrows(IOException.class, () ->
                apiClient.verifyPdfImpression(
                        "/icfc/parcela/gerarrecibo/6403614?tipo=0&numero-vias=2",
                        "abc123"
                )
        );
    }
}