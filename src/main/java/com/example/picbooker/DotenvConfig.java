package com.example.picbooker;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                // Only set if not already defined (from system environment)
                // if (System.getProperty(entry.getKey()) == null &&
                // System.getenv(entry.getKey()) == null) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                System.setProperty(entry.getKey(), entry.getValue());
                // }
            });

            System.out.println(".env loaded successfully.");
        } catch (Exception e) {
            System.out.println("Skipping .env loading — either missing or not needed in this environment.");
        }

    }
}