package io.github.kawaiicakes.vsutil.network.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

public class VSUtilPacketsImpl {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static int id() {
        return ID++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MOD_ID, "packets"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(UpdatePropellerPacketImpl.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdatePropellerPacketImpl::new)
                .encoder(UpdatePropellerPacketImpl::encode)
                .consumerMainThread(UpdatePropellerPacketImpl::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG payload) {
        INSTANCE.sendToServer(payload);
    }
}
