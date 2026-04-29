package br.com.pedroscheurer.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailModel {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String subject;
    private final String message;

    public EmailModel(String subject, String message) {
        this.timestamp = LocalDateTime.now();
        this.subject = subject;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return """
                Horário : %s
                Assunto : %s
                Mensagem: %s
                """.formatted(getTimestamp(), getSubject(), getMessage());
    }
}