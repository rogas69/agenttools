package com.sample.agenttools.config;

import com.hubspot.jinjava.Jinjava;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JinjaConfig {
    @Bean
    public Jinjava jinjava() {
        return new Jinjava();
    }
}

