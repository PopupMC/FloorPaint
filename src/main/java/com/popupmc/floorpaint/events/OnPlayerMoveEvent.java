package com.popupmc.floorpaint.events;

import com.popupmc.floorpaint.FloorPaint;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMoveEvent implements Listener {
    @EventHandler
    public void onPlayerMoveEvent (PlayerMoveEvent e) {

        // Stop here if not spawn
        if(e.getPlayer().getWorld() != FloorPaint.imperialCity)
            return;

        FloorPaint.plugin.doPaint(e.getPlayer());
    }
}
