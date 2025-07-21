package com.nickttd.neolightlevels.mixin.early;

import codechicken.nei.WorldOverlayRenderer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Overwrites the block light check in WorldOverlayRenderer to use '> 0' instead of '>= 8'.
 * Author: NeoLightLevelThresholds mod
 */
@Mixin(value = WorldOverlayRenderer.class, remap = false)
public abstract class MixinWorldOverlayRenderer {
    /**
     * Overwrite the getSpawnMode method to change the block light check.
     */
    @Overwrite
    public static byte getSpawnMode(Chunk chunk, int x, int y, int z) {
        // Change: block light must be 0 for spawn overlay, not <= 7
        if (chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15) > 0)
            return 0; // No spawn overlay drawn
        // Sky light logic unchanged
        return (byte) (chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15) >= 8 ? 1 : 2);
    }
}
