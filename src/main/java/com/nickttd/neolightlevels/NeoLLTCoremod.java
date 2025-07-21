package com.nickttd.neolightlevels;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("NeoLLTCoremod")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class NeoLLTCoremod implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        System.out.println("[NeoLightLevelThresholds] getMixinConfig called!");
        return "mixins.neollt.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        System.out.println("[NeoLightLevelThresholds] getMixins called!");
        return Arrays
            .asList("MixinEntityMob", "EntityRandAccessor", "MixinSpawnerAnimals", "MixinWorldOverlayRenderer");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
