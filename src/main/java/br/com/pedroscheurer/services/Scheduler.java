package br.com.pedroscheurer.services;

import br.com.pedroscheurer.configs.EnvConfig;
import br.com.pedroscheurer.models.EmailModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final WatchdogApiClient apiClient;
    private final EmailNotifier notifier;
    private final EnvConfig env;

    private final AtomicReference<String> tokenRef = new AtomicReference<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public Scheduler(WatchdogApiClient apiClient, EmailNotifier notifier, EnvConfig env) {
        this.apiClient = apiClient;
        this.notifier  = notifier;
        this.env       = env;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::renewToken,
                0,
                24,
                TimeUnit.HOURS
        );

        scheduler.scheduleAtFixedRate(
                this::verifyPdf,
                5,
                300,
                TimeUnit.SECONDS
        );

        log.info("Scheduler iniciado — token a cada 24h, verificação a cada 5min");
    }

    private void renewToken() {
        try {
            log.info("Renovando token...");
            String token = apiClient.requestToken(
                    "/paginacfc/auth/login",
                    """
                    {"email":"%s","password":"%s"}
                    """.formatted(env.robotEmail, env.robotPassword)
            );
            tokenRef.set(token);
            log.info("Token renovado com sucesso");
        } catch (Exception e) {
            log.error("Falha ao renovar token: {}", e.getMessage());
            sendAlert("[ALERTA] Falha ao renovar token", e.getMessage());
        }
    }

    private void verifyPdf() {
        String token = tokenRef.get();

        if (token == null) {
            log.warn("Token ainda não disponível, aguardando próximo ciclo...");
            return;
        }

        try {
            log.info("Verificando impressão PDF...");
            String response = apiClient.verifyPdfImpression(
                    "/icfc/parcela/gerarrecibo/6403614?tipo=0&numero-vias=2",
                    token
            );
            log.info("PDF verificado com sucesso: {}", response);
        } catch (Exception e) {
            log.error("Falha ao verificar PDF: {}", e.getMessage());
            sendAlert("[ALERTA] Falha ao gerar PDF", e.getMessage());
        }
    }

    private void sendAlert(String subject, String message) {
        try {
            EmailModel email = new EmailModel(subject, message);
            notifier.send(email.getSubject(),
                    "Horário: " + email.getTimestamp() + "\n\n" + email.getMessage());
            log.info("Email de alerta enviado");
        } catch (Exception e) {
            log.error("Falha ao enviar email: {}", e.getMessage());
        }
    }

    public void stop() {
        scheduler.shutdown();
        log.info("Scheduler encerrado");
    }
}