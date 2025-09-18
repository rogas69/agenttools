package com.sample.agenttools.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@Configuration
public class SakilaDbConfig {

    @Bean(name = "sakilaDataSource")
    @ConfigurationProperties(prefix = "spring.sakila-datasource")
    public DataSource sakilaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sakilaJdbcTemplate")
    public JdbcTemplate sakilaJdbcTemplate(@Qualifier("sakilaDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

