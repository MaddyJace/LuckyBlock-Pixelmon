package net.luckyblockpixelmon.maddyjace.api;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class ForgeWorld {

    public static WorldServer getWorld(String worldName) {
        for (WorldServer world : DimensionManager.getWorlds()) {
            if (world == null) continue;
            if (world.getWorldInfo().getWorldName().equalsIgnoreCase(worldName)) {
                return world;
            }
        }
        return null;
    }

}
