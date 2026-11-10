package dev.aaron.inventory;

import dev.aaron.inventory.config.AdminCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(AdminCredentials.class)
public class InventoryConsoleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(InventoryConsoleApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InventoryConsoleApplication.class);
    }
}
