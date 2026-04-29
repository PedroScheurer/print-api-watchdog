package br.com.pedroscheurer.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotifier {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String from;
    private final String to;

    public EmailNotifier(String host, int port,
                         String username, String password,
                         String from, String to) {
        this.host     = host;
        this.port     = port;
        this.username = username;
        this.password = password;
        this.from     = from;
        this.to       = to;
    }

    public void send(String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            host);
        props.put("mail.smtp.port",            String.valueOf(port));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        IO.println("Email de alerta enviado para: " + to);
    }
}