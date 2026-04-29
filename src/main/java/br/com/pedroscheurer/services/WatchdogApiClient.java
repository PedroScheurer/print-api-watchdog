package br.com.pedroscheurer.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WatchdogApiClient {

    private final HttpClient client;
    private final String baseUrl;
    ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(WatchdogApiClient.class);


    public WatchdogApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client  = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public WatchdogApiClient(String baseUrl, HttpClient client) {
        this.baseUrl = baseUrl;
        this.client  = client;
    }

    public String verifyPdfImpression(String path, String token) throws IOException, InterruptedException {
        log.info("Verificando impressão PDF — path: {}", path);
        HttpRequest requestPdf = buildRequestWithToken(path, token);

        HttpResponse<String> response =
                client.send(requestPdf, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (isStatusCodeOk(status)) {
            return response.body();
        }

        log.error("Falha ao gerar PDF — status: {}", status);
        throw new IOException(
                "Requisição falhou — status: " + status +
                        " | body: " + response.body()
        );
    }

    public String requestToken(String path, String body) throws IOException, InterruptedException {
        log.info("Requisitando token — path: {}", path);
        HttpRequest requestToken = buildRequest(path, body);

        HttpResponse<String> response = client.send(requestToken, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (isStatusCodeOk(status)) {
            log.info("Token obtido com sucesso");
            JsonNode node = mapper.readTree(response.body());
            return node.get("token").asText();
        }

        log.error("Falha ao obter token — status: {}", status);
        throw new IOException(
                "Requisição falhou — status: " + status +
                        " | body: " + response.body()
        );
    }

    private HttpRequest buildRequestWithToken(String path, String token) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
    }


    private HttpRequest buildRequest(String path, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private boolean isStatusCodeOk(int status){
        return status >= 200 && status < 300;
    }
}
