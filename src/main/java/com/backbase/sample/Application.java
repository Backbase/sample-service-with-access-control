package com.backbase.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main class for Sample Presentation Service.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    /**
     * Application Start.
     *
     * @param application {@link SpringApplicationBuilder}
     * @return {@link SpringApplicationBuilder}
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * Application start.
     *
     * @param args input application arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
