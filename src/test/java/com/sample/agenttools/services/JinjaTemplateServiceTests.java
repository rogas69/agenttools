package com.sample.agenttools.services;

import com.hubspot.jinjava.Jinjava;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ContextConfiguration;
import com.sample.agenttools.config.JinjaConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {JinjaTemplateService.class, JinjaConfig.class})
@TestPropertySource(properties = "database.path=./database")
@ContextConfiguration(classes = {JinjaTemplateService.class, JinjaConfig.class})
class JinjaTemplateServiceTests {
    @Autowired
    private JinjaTemplateService jinjaTemplateService;

    @Test
    void testRenderString_basic() {
        String template = "Hello, {{ name }}!";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "World");
        String result = jinjaTemplateService.renderString(template, context);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testRenderString_emptyContext() {
        String template = "Hello, {{ name }}!";
        Map<String, Object> context = Collections.emptyMap();
        String result = jinjaTemplateService.renderString(template, context);
        assertEquals("Hello, !", result);
    }

    @Test
    void testRenderString_emptyTemplate() {
        String template = "";
        Map<String, Object> context = new HashMap<>();
        String result = jinjaTemplateService.renderString(template, context);
        assertEquals("", result);
    }

    @Test
    void testRenderFile_basic(@TempDir java.nio.file.Path tempDir) throws IOException {
        File file = tempDir.resolve("template.jinja").toFile();
        Files.writeString(file.toPath(), "Hi, {{ who }}!");
        Map<String, Object> context = new HashMap<>();
        context.put("who", "Agent");
        String result = jinjaTemplateService.renderFile(file, context);
        assertEquals("Hi, Agent!", result);
    }

    @Test
    void testRenderFile_emptyFile(@TempDir java.nio.file.Path tempDir) throws IOException {
        File file = tempDir.resolve("empty.jinja").toFile();
        Files.writeString(file.toPath(), "");
        Map<String, Object> context = new HashMap<>();
        String result = jinjaTemplateService.renderFile(file, context);
        assertEquals("", result);
    }

    @Test
    void testRenderFile_fileNotFound() {
        File file = new File("nonexistent.jinja");
        Map<String, Object> context = new HashMap<>();
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            jinjaTemplateService.renderFile(file, context);
        });
        assertTrue(thrown.getMessage().contains("nonexistent.jinja"));
    }
}
