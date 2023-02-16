package no.ntnu.nms;

import no.ntnu.nms.logging.Logging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class App {

    public static void main(String[] args) {

        try {
            Logging.setUpLogger("ALL");
        } catch (IOException e) {
            System.out.println("Could not initialize logger: " + e.getMessage());
            System.exit(0);
        }

        // Starts the API WebServer and hosts the API from the ApiApp class.
        ApplicationContext ctx = SpringApplication.run(App.class, args);
        ctx.getDisplayName();
    }
}
