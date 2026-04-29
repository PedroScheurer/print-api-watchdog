package br.com.pedroscheurer.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailModelTest {

    @Test
    void shouldHaveTimestampInInstantiation() {
        EmailModel email = new EmailModel("Assunto", "Mensagem");
        assertNotNull(email.getTimestamp());
    }

    @Test
    void shouldReturnCorrectSubjectAndMessage() {
        EmailModel email = new EmailModel("Alerta PDF", "Erro ao gerar");
        assertEquals("Alerta PDF", email.getSubject());
        assertEquals("Erro ao gerar", email.getMessage());
    }

    @Test
    void toStringShouldHaveAllInfo() {
        EmailModel email = new EmailModel("Assunto", "Mensagem");
        String str = email.toString();
        assertTrue(str.contains("Assunto"));
        assertTrue(str.contains("Mensagem"));
        assertTrue(str.contains(email.getTimestamp()));
    }
}