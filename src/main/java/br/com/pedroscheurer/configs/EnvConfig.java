package br.com.pedroscheurer.configs;

public class EnvConfig {
    public final String mailUsername;
    public final String mailPassword;
    public final String mailFrom;
    public final String mailTo;
    public final String robotEmail;
    public final String robotPassword;


    public EnvConfig() {
        this.mailUsername = getOrThrow("MAIL_USERNAME");
        this.mailPassword = getOrThrow("MAIL_PASSWORD");
        this.mailFrom = getOrThrow("MAIL_FROM");
        this.mailTo = getOrThrow("MAIL_TO");
        this.robotEmail = getOrThrow("ROBOT_EMAIL");
        this.robotPassword = getOrThrow("ROBOT_PASSWORD");
    }

    private String getOrThrow(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Variável de ambiente obrigatória não encontrada: " + key
            );
        }
        return value;
    }
}
