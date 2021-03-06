package dev.fxcte.creepyware.manager;

import dev.fxcte.creepyware.util.DisplayUtil;
import dev.fxcte.creepyware.util.NoStackTraceThrowable;
import dev.fxcte.creepyware.util.SystemUtil;
import dev.fxcte.creepyware.util.URLReader;

import java.util.ArrayList;
import java.util.List;

public
class HWIDManager {

    /**
     * Your pastebin URL goes inside the empty string below.
     * It should be a raw pastebin link, for example: pastebin.com/raw/pasteid
     */

    public static final String pastebinURL = "https://pastebin.com/raw/JVRvfij3";

    public static List <String> hwids = new ArrayList <>();

    public static
    void hwidCheck() {
        hwids = URLReader.readURL();
        boolean isHwidPresent = hwids.contains(SystemUtil.getSystemInfo());
        if (! isHwidPresent) {
            DisplayUtil.Display();
            throw new NoStackTraceThrowable("");
        }
    }
}