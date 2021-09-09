package dev.fxcte.creepyware.manager;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.modules.client.HUD;
import dev.fxcte.creepyware.features.notifications.Notifications;

import java.util.ArrayList;

public
class NotificationManager {
    private final ArrayList <Notifications> notifications = new ArrayList();

    public
    void handleNotifications(int posY) {
        for (int i = 0; i < this.getNotifications().size(); ++ i) {
            this.getNotifications().get(i).onDraw(posY);
            posY -= CreepyWare.moduleManager.getModuleByClass(HUD.class).renderer.getFontHeight() + 5;
        }
    }

    public
    void addNotification(String text, long duration) {
        this.getNotifications().add(new Notifications(text, duration));
    }

    public
    ArrayList <Notifications> getNotifications() {
        return this.notifications;
    }
}

