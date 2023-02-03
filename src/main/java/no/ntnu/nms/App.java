package no.ntnu.nms;

import no.ntnu.nms.domain_model.Pool;
import no.ntnu.nms.domain_model.PoolRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class App {

    public static void main(String[] args) {

        // Starts the API WebServer and hosts the API from the ApiApp class.
        ApplicationContext ctx = SpringApplication.run(App.class, args);
        System.out.println("Application name: " + ctx.getDisplayName());

        PoolRegistry poolreg = PoolRegistry.getInstance();
        poolreg.addPool(new Pool("test1", 23, "A tester pool"));
        poolreg.addPool(new Pool("test2", 23, "A tester pool"));
        poolreg.addPool(new Pool("test3", 23, "A tester pool"));

    }
}
