package io.github.kawaiicakes.vsutil.fabric;

import io.github.kawaiicakes.vsutil.VSUtil;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class VSUtilFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();
        VSUtil.init();
    }
}
