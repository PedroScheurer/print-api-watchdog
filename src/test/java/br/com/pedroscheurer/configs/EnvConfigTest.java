package br.com.pedroscheurer.configs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvConfigTest {

    @Test
    void shouldThrowExceptionWhenNotFoundEnvironmentVariable(){
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                EnvConfig::new
        );

        assertTrue(ex.getMessage().contains("MAIL_USERNAME"));
    }
}