package me.perry.yoink.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final
class FileUtil {
    public static
    List <File> getFiles (String dir) {
        try {
            try (Stream <Path> paths = Files.walk(Paths.get(dir))) {
                return paths.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            }
        } catch (Exception ignored) {
        }
        return new ArrayList <>();
    }

    public static
    Optional <File> getFile (String name) {
        return Optional.of(new File(name));
    }

    public static
    String randomString () {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}