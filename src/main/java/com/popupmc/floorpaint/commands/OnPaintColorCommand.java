package com.popupmc.floorpaint.commands;

import com.popupmc.floorpaint.FloorPaint;
import com.popupmc.floorpaint.colors.BaseColors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OnPaintColorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "The console can't walk around in the world");
            return true;
        }

        Player p = (Player) sender;

        if(args.length != 1) {
            p.sendMessage(ChatColor.RED + "Error: Wrong format, randomizing color");
            FloorPaint.playerColors.remove(p.getUniqueId());
            return false;
        }

        String color = args[0];

        if(color.equalsIgnoreCase("random")) {
            FloorPaint.playerColors.remove(p.getUniqueId());
            p.sendMessage(ChatColor.GOLD + "Paint color randomized");
            return true;
        }
        else if(color.equalsIgnoreCase("none")) {
            FloorPaint.playerColors.put(p.getUniqueId(), BaseColors.NONE);
            p.sendMessage(ChatColor.GOLD + "Paint color removed");
            return true;
        }

        BaseColors colorVal;
        try {
            colorVal = BaseColors.valueOf(color.toUpperCase());
            FloorPaint.playerColors.put(p.getUniqueId(), colorVal);
            p.sendMessage(ChatColor.GOLD + "Paint color set to " + color);
        }
        catch (IllegalArgumentException ex) {
            FloorPaint.playerColors.remove(p.getUniqueId());
            p.sendMessage(ChatColor.RED + "Error: Invalid color! Picking random color.");
        }

        return true;
    }
}
