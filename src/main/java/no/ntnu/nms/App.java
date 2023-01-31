package no.ntnu.nms;

import no.ntnu.nms.api.ApiApp;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;


public class App {

    public static void main(String[] args) {

        // Starts the API WebServer and hosts the API from the ApiApp class.
        ApplicationContext ctx = SpringApplication.run(ApiApp.class, args);
        System.out.println("Application name: " + ctx.getDisplayName());

    }
}
