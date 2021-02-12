package com.popupmc.floorpaint;

import com.popupmc.floorpaint.colors.BaseColors;
import com.popupmc.floorpaint.colors.Colors;
import com.popupmc.floorpaint.commands.OnPaintColorCommand;
import com.popupmc.floorpaint.events.OnPlayerMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FloorPaint extends JavaPlugin implements Listener, CommandExecutor {
    @Override
    public void onEnable() {
        plugin = this;

        imperialCity = Bukkit.getWorld("imperial_city");
        if(imperialCity == null) {
            getLogger().warning("ERROR: imperial_city is null");
            this.setEnabled(false);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new OnPlayerMoveEvent(), this);
        Objects.requireNonNull(this.getCommand("paint-color")).setExecutor(new OnPaintColorCommand());

        // Log enabled status
        getLogger().info("FloorPaint is enabled.");
    }

    // Log disabled status
    @Override
    public void onDisable() {
        getLogger().info("FloorPaint is disabled");
    }

    public void doPaint(Player p) {

        // Get location
        Location location = p.getLocation();

        // Get block under player
        Block block = imperialCity.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());

        // Get block at player
        Block blockAbove = imperialCity.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // If not a smooth stone then stop here
        // We only paint smooth stone
        // An exception is made if the block is in the middle of animating as we just reset the animation
        if(!blockList.containsKey(block) &&
                block.getType() != Material.SMOOTH_STONE_SLAB &&
                block.getType() != Material.SMOOTH_STONE &&
                block.getType() != Material.STONE_BRICKS &&
                block.getType() != Material.GLASS &&
                !blockAbove.getType().toString().endsWith("_CARPET") &&
                !block.getType().toString().endsWith("_WOOL") &&
                //block.getType() != Material.SMOOTH_QUARTZ &&
                //block.getType() != Material.QUARTZ_BLOCK &&
                block.getType() != Material.OAK_PLANKS)
            return;

        // Fixes a bug
        if(block.getType().toString().endsWith("_CARPET"))
            return;

        // Obtain paint color
        BaseColors baseColorCode = getPlayerBaseColor(p);

        // Do nothing if color code is in the negatives
        if(baseColorCode == BaseColors.NONE)
            return;

        // Create or update animation data length and set initial color
        BlockAnimationData data;

        if(blockAbove.getType().toString().endsWith("_CARPET")) {
            data = updateAnimationEntry(blockAbove, animStepLength);
            data.darker = true;
            data.matSuffix = "_CARPET";
            data.curColor = BaseColors.lightEquiv(baseColorCode);
            blockAbove.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));
        }
        else if(block.getType().toString().endsWith("_WOOL")) {
            data = updateAnimationEntry(block, animStepLength);
            data.darker = true;
            data.matSuffix = "_WOOL";
            data.curColor = BaseColors.lightEquiv(baseColorCode);
            block.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));
        }
//        else if(block.getType() == Material.SMOOTH_QUARTZ ||
//                block.getType() == Material.QUARTZ_BLOCK ||
//                block.getType().toString().endsWith("_GLAZED_TERRACOTTA")) {
//            data = updateAnimationEntry(block, animStepLength);
//            data.darker = false;
//            data.matSuffix = "_GLAZED_TERRACOTTA";
//            data.curColor = BaseColors.darkEquiv(baseColorCode);
//            block.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));
//        }
        else if(block.getType() == Material.OAK_PLANKS ||
                block.getType().toString().endsWith("_STAINED_GLASS") ||
                block.getType() == Material.GLASS) {
            data = updateAnimationEntry(block, animStepLength);
            data.darker = true;
            data.matSuffix = "_STAINED_GLASS";
            data.curColor = BaseColors.lightEquiv(baseColorCode);
            block.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));
        }
        else {
            data = updateAnimationEntry(block, animStepLength);
            data.curColor = BaseColors.darkEquiv(baseColorCode);
            data.darker = false;
            data.matSuffix = "_CONCRETE";
            block.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));
        }

        // Request timer re-run
        runAnimationLoop();
    }

    public BlockAnimationData updateAnimationEntry(Block block, int length) {
        BlockAnimationData data;

        // Get or create animation data
        if(blockList.containsKey(block)) {
            data = blockList.get(block);
            data.secondsLeft = length;
        }
        else
            data = new BlockAnimationData(length, block);

        // Update it
        blockList.put(block, data);
        return data;
    }

    public BaseColors getPlayerBaseColor(Player p) {

        // Get players UUID
        UUID uuid = p.getUniqueId();

        // Randomize a color if it doesn't exist
        if(!playerColors.containsKey(uuid))
            playerColors.put(uuid, BaseColors.random());

        // Return color
        return playerColors.get(uuid);
    }

    public void stepColor(Block block) {
        BlockAnimationData data = blockList.get(block);

        if(data.darker)
            data.curColor = Colors.darker(data.curColor);
        else
            data.curColor = Colors.brighter(data.curColor);

        block.setType(Material.valueOf(data.curColor.toString() + data.matSuffix));

        if(data.curColor != Colors.WHITE && !data.darker)
            updateAnimationEntry(block, animStepLength);
        else if(data.curColor != Colors.BLACK && data.darker)
            updateAnimationEntry(block, animStepLength);
        else {
            blockList.get(block).restoreBlock(block);
            blockList.remove(block);
        }
    }

    // Step animation counters every 1 second
    public void runAnimationLoop() {

        // Don't do anything if the timer is ongoing
        if(animationTimer != null) {
            return;
        }

        // Do another animation step
        animationTimer = new BukkitRunnable() {
            @Override
            public void run() {
                // If blocklist is empty then stop here
                if(blockList.isEmpty()) {
                    animationTimer = null;
                    return;
                }

                // Do a shallow copy of all the blocks, this is because they will be removed while the loop is running
                // which can cause errors
                for(Map.Entry<Block, BlockAnimationData> blockEntry : new HashMap<>(blockList).entrySet()) {

                    // Countdown timer
                    int timer = blockEntry.getValue().secondsLeft - 1;
                    blockEntry.getValue().secondsLeft = timer;

                    // Step color when reached 0
                    if(timer <= 0)
                        stepColor(blockEntry.getKey());
                }

                // Mark task complete, do another loop
                animationTimer = null;
                runAnimationLoop();
            }
        }.runTaskLater(this, animUpdateInterval);
    }

    // Ref to the world
    public static World imperialCity;

    // Blocks that are being animated, contains animation state and data to restore blocks after animation
    public static final HashMap<Block, BlockAnimationData> blockList = new HashMap<>();

    // Let the player change their color
    public static final HashMap<UUID, BaseColors> playerColors = new HashMap<>();

    // Is the timer moving?
    BukkitTask animationTimer = null;

    // Intervals between animation steps
    public static final int animStepLength = 2;

    // Ticks between each interval
    public static final int animUpdateInterval = 20;

    public static FloorPaint plugin;
}
