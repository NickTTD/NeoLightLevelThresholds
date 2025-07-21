package com.nickttd.neolightlevels.mixin.early;

import java.util.Random;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityRandAccessor {

    @Accessor("rand")
    Random getRand();
}
