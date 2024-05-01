package com.codehunter.springshelldemo;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GlobalStack {
    private static final Logger log = LogManager.getLogger(GlobalStack.class);

    private String currentDirectory = "/";

    public void setCurrentDirectory(String currentDirectory) {
        log.info("Change directory to: {}", currentDirectory);
        this.currentDirectory = currentDirectory;
    }
}