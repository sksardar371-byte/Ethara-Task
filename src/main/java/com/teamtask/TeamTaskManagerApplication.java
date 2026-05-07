package com.teamtask;

import com.teamtask.model.Role;
import com.teamtask.model.User;
import com.teamtask.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class TeamTaskManagerApplication {
    public static void main(String[] args) {
        configureRailwayDatabaseUrl();
        SpringApplication.run(TeamTaskManagerApplication.class, args);
    }

    private static void configureRailwayDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank() || System.getenv("SPRING_DATASOURCE_URL") != null) {
            return;
        }
        URI uri = URI.create(databaseUrl);
        String[] userInfo = uri.getUserInfo() == null ? new String[]{"", ""} : uri.getUserInfo().split(":", 2);
        String username = URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8);
        String password = userInfo.length > 1 ? URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8) : "";
        String port = uri.getPort() > 0 ? ":" + uri.getPort() : "";
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + port + uri.getPath();
        if (uri.getQuery() != null) {
            jdbcUrl += "?" + uri.getQuery();
        }
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", username);
        System.setProperty("spring.datasource.password", password);
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByEmail("admin@example.com")) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                users.save(admin);
            }
        };
    }
}
