package com.popupmc.floorpaint;

import com.popupmc.floorpaint.colors.Colors;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockAnimationData {
    public BlockAnimationData(int secondsLeft, Block originalBlock) {
        this.secondsLeft = secondsLeft;

        this.originalMaterial = originalBlock.getType();
        this.originalData = originalBlock.getBlockData().clone();
    }

    public void restoreBlock(Block block) {
        block.setType(originalMaterial);
        block.setBlockData(originalData);
    }

    int secondsLeft;
    Colors curColor = Colors.BLACK;

    Material originalMaterial;
    BlockData originalData;

    boolean darker = false;
    String matSuffix;
}
