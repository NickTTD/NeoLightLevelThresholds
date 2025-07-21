package com.nickttd.neolightlevels.mixin.early;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Overwrites isValidLightLevel to match modern Minecraft behavior (1.18+).
 * Mobs can spawn when the combined light level is 7 or below.
 * Author: NeoLightLevelThresholds mod
 */
@Mixin(EntityMob.class)
public abstract class MixinEntityMob {

    static {
        System.out.println("[NeoLightLevelThresholds] MixinEntityMob loaded: modern mob spawning behavior");
    }

    /**
     * Overwritten: Modern mob spawning logic that checks actual light levels.
     * This matches the behavior from modern Minecraft versions.
     */
    @Overwrite
    protected boolean isValidLightLevel() {
        EntityMob self = (EntityMob) (Object) this;
        int i = MathHelper.floor_double(self.posX);
        int j = MathHelper.floor_double(self.boundingBox.minY);
        int k = MathHelper.floor_double(self.posZ);

        // First check: No artificial light sources (block light must be 0)
        int blockLight = self.worldObj.getSavedLightValue(EnumSkyBlock.Block, i, j, k);

        if (blockLight > 0) {
            // System.out.println("[NeoLightLevelThresholds] Light check - pos: " + i + "," + j + "," + k +
            // " blockLight: " + blockLight + " canSpawn: false (artificial light)");
            return false;
        }

        // Second check: Combined light level must be 7 or below (accounts for time of day)
        int combinedLight = self.worldObj.getBlockLightValue(i, j, k);
        // Match vanilla: during thunderstorms, temporarily set skylightSubtracted to 10
        if (self.worldObj.isThundering()) {
            int oldSkylightSubtracted = self.worldObj.skylightSubtracted;
            self.worldObj.skylightSubtracted = 10;
            combinedLight = self.worldObj.getBlockLightValue(i, j, k);
            self.worldObj.skylightSubtracted = oldSkylightSubtracted;
        }
        int randomThreshold = ((EntityRandAccessor) self).getRand()
            .nextInt(8);
        boolean canSpawn = combinedLight <= randomThreshold;

        // Debug info - print the random threshold and combinedLight for troubleshooting randomness
        // System.out.println("[NeoLightLevelThresholds] Random light check - pos: " + i + "," + j + "," + k +
        // " combinedLight: " + combinedLight + " randomThreshold: " + randomThreshold + " canSpawn: " + canSpawn);

        return canSpawn;
    }
}
