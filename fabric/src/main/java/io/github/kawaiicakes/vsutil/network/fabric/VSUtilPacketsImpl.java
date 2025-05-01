package io.github.kawaiicakes.vsutil.network.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

public class VSUtilPacketsImpl {
    public static ResourceLocation UPDATE_PROPELLER = new ResourceLocation(MOD_ID, "update_propeller");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PROPELLER, UpdatePropellerPacketImpl::receive);
    }

    // unnecessary on Fabric; it will run based on side effects from argument
    public static <MSG> void sendToServer(MSG payload) {}
}
