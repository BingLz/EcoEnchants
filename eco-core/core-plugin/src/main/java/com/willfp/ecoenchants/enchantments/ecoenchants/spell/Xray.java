package com.willfp.ecoenchants.enchantments.ecoenchants.spell;

import com.willfp.eco.util.TeamUtils;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.itemtypes.Spell;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Xray extends Spell {
    public Xray() {
        super("xray");
    }

    @Override
    public void onUse(@NotNull final Player player,
                      final int level,
                      @NotNull final PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (block == null) {
            return;
        }

        Set<Block> toReveal = new HashSet<>();

        int size = this.getConfig().getInt(EcoEnchants.CONFIG_LOCATION + "blocks-per-level") * level;

        int ticks = this.getConfig().getInt(EcoEnchants.CONFIG_LOCATION + "ticks");

        List<Material> materials = new ArrayList<>();

        for (String materialName : this.getConfig().getStrings(EcoEnchants.CONFIG_LOCATION + "blocks")) {
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material != null) {
                materials.add(material);
            }
        }

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    Block block1 = block.getWorld().getBlockAt(block.getLocation().clone().add(x, y, z));

                    if (!materials.contains(block1.getType())) {
                        continue;
                    }

                    boolean hidden = true;

                    for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN}) {
                        if (block1.getRelative(face).getType() == Material.AIR) {
                            hidden = false;
                        }
                    }

                    if (!hidden) {
                        continue;
                    }

                    toReveal.add(block1);
                }
            }
        }

        toReveal.forEach(block1 -> {
            Shulker shulker = (Shulker) block1.getWorld().spawnEntity(block1.getLocation(), EntityType.SHULKER);
            shulker.setInvulnerable(true);
            shulker.setSilent(true);
            shulker.setAI(false);
            shulker.setGravity(false);
            shulker.setGlowing(true);
            shulker.setInvisible(true);

            if (this.getConfig().getBool(EcoEnchants.CONFIG_LOCATION + "color-glow")) {
                Team team = TeamUtils.getMaterialColorTeam(block1.getType());
                team.addEntry(shulker.getUniqueId().toString());
            }

            this.getPlugin().getScheduler().runLater(shulker::remove, ticks);
        });
    }
}
