package io.github.kawaiicakes.vsutil.fabric.client;

import io.github.kawaiicakes.vsutil.VSUtil;
import net.fabricmc.api.ClientModInitializer;

public class VSUtilFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VSUtil.initClient();
    }
}
