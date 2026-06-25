package com.connectit.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("[DataSourceConfig] Checking connectivity to configured database: {}", dbUrl);
        try {
            Class.forName(driverClassName);
            DriverManager.setLoginTimeout(3); // 3 seconds timeout
            try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
                log.info("[DataSourceConfig] MySQL connection successful. Initializing Hikari connection pool.");
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dbUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName(driverClassName);
                config.setMaximumPoolSize(20);
                config.setMinimumIdle(5);
                config.setIdleTimeout(300000);
                config.setConnectionTimeout(20000);
                return new HikariDataSource(config);
            }
        } catch (Exception e) {
            log.error("[DataSourceConfig] Database connection check failed: {}", e.getMessage());
            throw new RuntimeException("Database connection check failed", e);
        }
    }
}
