package no.ntnu.nms;

import no.ntnu.nms.domain_model.Pool;
import no.ntnu.nms.database.repositories.PoolRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class App {

    public static void main(String[] args) {

        // Starts the API WebServer and hosts the API from the ApiApp class.
        ApplicationContext ctx = SpringApplication.run(App.class, args);
        System.out.println("Application name: " + ctx.getDisplayName());
    }

    @Bean
    public CommandLineRunner demo(PoolRepository repository) {
        return args -> {
            repository.save(new Pool("4k_stream"));
            repository.save(new Pool("2k_stream"));

            for (Pool pool : repository.findAll()) {
                System.out.println(pool);
            }

            System.out.println(repository.findByMediaFunction("4k_stream"));

        };
    }
}
