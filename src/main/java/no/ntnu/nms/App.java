package no.ntnu.nms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class App {

    public static void main(String[] args) {

        // Starts the API WebServer and hosts the API from the ApiApp class.
        ApplicationContext ctx = SpringApplication.run(App.class, args);
        System.out.println("Application name: " + ctx.getDisplayName());
    }
}
