package com.codehunter.springshelldemo;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@ShellComponent
@RequiredArgsConstructor
public class FileCommand {
    private static final Logger log = LogManager.getLogger(FileCommand.class);
    private final GlobalStack globalStack;
    private final TerminalUIBuilder builder;

    @ShellMethod("Current directory")
    public String cd(String uri) {
        if (isAbsolutePath(uri)) {
            globalStack.setCurrentDirectory(uri);
            return "change directory successfully";
        } else {
            var newUri = Paths.get(globalStack.getCurrentDirectory()).resolve(uri).toAbsolutePath();
            if (Files.isDirectory(newUri)) {
                globalStack.setCurrentDirectory(newUri.toAbsolutePath().normalize().toString());
                return "change directory successfully";
            }
            return "input is not a directory";
        }
    }

    private boolean isAbsolutePath(String uri) {
        var path = Paths.get(uri);
        return path.isAbsolute() && Files.isDirectory(path);
    }

    @ShellMethod("List all file in directory")
    public String ls() throws IOException {
        var path = Path.of(globalStack.getCurrentDirectory());
        var result = new StringBuilder();
        if (Files.isDirectory(path)) {

            try (Stream<Path> list = Files.list(path);) {
                list.forEach(e -> result.append(e).append('\n'));
            }
        }
        return result.toString();
    }

    @ShellMethod("Current working directory")
    public String pwd() {
        return Paths.get(globalStack.getCurrentDirectory()).toAbsolutePath().toString();
    }

    @ShellMethod("Create new folder")
    public void mkdir(String filename) throws IOException {
        var path = Path.of(globalStack.getCurrentDirectory()).toAbsolutePath().resolve(filename);
        var newPath = Files.createDirectory(path);
        log.info("Create folder successfully: {}", newPath);
    }


    @ShellMethod("Sample UI")
    void sampleUI() {
        TerminalUI ui = builder.build();
        BoxView view = new BoxView();
        ui.configure(view);
        view.setDrawFunction((screen, rect) -> {
            screen.writerBuilder()
                    .build()
                    .text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
            return rect;
        });
        ui.setRoot(view, true);
        EventLoop eventLoop = ui.getEventLoop();
        eventLoop.keyEvents()
                .subscribe(event -> {
                    if (event.getPlainKey() == KeyEvent.Key.q && event.hasCtrl()) {
                        eventLoop.dispatch(ShellMessageBuilder.ofInterrupt());
                    }
                });
        ui.run();
    }
}
