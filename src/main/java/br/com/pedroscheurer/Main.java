package br.com.pedroscheurer;

import br.com.pedroscheurer.configs.EnvConfig;
import br.com.pedroscheurer.services.WatchdogApiClient;
import br.com.pedroscheurer.services.EmailNotifier;
import br.com.pedroscheurer.services.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);


    static void main() {
        log.info("Iniciando aplicação print-api-watchdog");

        EnvConfig env = new EnvConfig();
        WatchdogApiClient apiClient = new WatchdogApiClient("https://api.icfc.com.br");
        EmailNotifier notifier = new EmailNotifier(
                "smtp.gmail.com",
                587,
                env.mailUsername,
                env.mailPassword,
                env.mailFrom,
                env.mailTo
        );

        Scheduler scheduler = new Scheduler(apiClient, notifier, env);
        scheduler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));

    }
}
