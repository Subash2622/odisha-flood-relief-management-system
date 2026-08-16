package com.odisha.floodrelief;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class FloodReliefApplication {

    private static final Logger log = LoggerFactory.getLogger(FloodReliefApplication.class);

    public static void main(String[] args) {
        prepareLogging();
        SpringApplication.run(FloodReliefApplication.class, args);
        log.info("============================================================");
        log.info(" Odisha Flood Relief & NGO Management System started");
        log.info(" Author : Subash Chandra Sahoo | Software Engineer");
        log.info(" Logs   : see project /log folder (console + dated files)");
        log.info(" Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.");
        log.info("============================================================");
    }

    /**
     * Creates project /log folder and sets a dated session log file name.
     * Example: odisha-flood-relief-2026-08-16_13-05-22.log
     */
    private static void prepareLogging() {
        try {
            Path cwd = Paths.get("").toAbsolutePath().normalize();
            Path logDir;
            if (Files.isDirectory(cwd.resolve("log"))) {
                // Started from project root
                logDir = cwd.resolve("log");
            } else if (cwd.getFileName() != null
                    && "backend".equalsIgnoreCase(cwd.getFileName().toString())
                    && cwd.getParent() != null) {
                // Started from backend module
                logDir = cwd.getParent().resolve("log");
            } else {
                logDir = cwd.resolve("log");
            }

            Files.createDirectories(logDir);
            Files.createDirectories(logDir.resolve("archive"));

            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            Path sessionFile = logDir.resolve("odisha-flood-relief-" + stamp + ".log");

            System.setProperty("LOG_PATH", logDir.toString());
            System.setProperty("LOG_FILE", sessionFile.toString());

            System.out.println("[Startup] Log directory : " + logDir);
            System.out.println("[Startup] Session log   : " + sessionFile.getFileName());
        } catch (Exception e) {
            System.err.println("[Startup] Could not prepare log folder: " + e.getMessage());
        }
    }
}
