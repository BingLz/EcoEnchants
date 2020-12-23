package com.willfp.eco.core.proxy.v1_16_R2;

import com.willfp.eco.core.proxy.proxies.BlockBreakProxy;
import net.minecraft.server.v1_16_R2.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BlockBreak implements BlockBreakProxy {
    @Override
    public void breakBlock(@NotNull final Player player,
                           @NotNull final Block block) {
        ((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }
}
