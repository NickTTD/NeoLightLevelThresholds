package com.nickttd.neolightlevels;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class NeoLLTLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.neollt.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Collections.emptyList();
    }
}
