package com.nickttd.neolightlevels.mixin.early;

import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public interface EntityRandAccessor {
    @Accessor("rand")
    Random getRand();
}
