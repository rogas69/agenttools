package com.sample.agenttools.services;

import com.hubspot.jinjava.Jinjava;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
@Service
public class JinjaTemplateService {
    private final Jinjava jinjava;

    @Autowired
    public JinjaTemplateService(Jinjava jinjava) {
        this.jinjava = jinjava;
    }

    /**
     * Render a Jinja2 template from a string.
     */
    public String renderString(String template, Map<String, Object> context) {
        return jinjava.render(template, context);
    }

    /**
     * Render a Jinja2 template from a file.
     */
    public String renderFile(File file, Map<String, Object> context) throws IOException {
        try {
            String templateContent = Files.readString(file.toPath());
            return jinjava.render(templateContent, context);
        } catch (IOException e) {
            log.error("An exception occurred when rendering the file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
