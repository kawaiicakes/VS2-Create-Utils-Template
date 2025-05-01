package io.github.kawaiicakes.vsutil.network.fabric;

import io.github.kawaiicakes.vsutil.network.UpdatePropellerPacket;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;

public class UpdatePropellerPacketImpl extends UpdatePropellerPacket {
    public static UpdatePropellerPacket create(
            ResourceKey<Level> level, BlockPos pos,
            double force, float maxSpeed, float accel, boolean finalize
    ) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeResourceKey(level);
        buf.writeBlockPos(pos);
        buf.writeDouble(force);
        buf.writeFloat(maxSpeed);
        buf.writeFloat(accel);
        buf.writeBoolean(finalize);
        ClientPlayNetworking.send(VSUtilPacketsImpl.UPDATE_PROPELLER, buf);

        // the return isn't needed on the Fabric impl for UpdatePropellerPacket
        return new UpdatePropellerPacketImpl();
    }

    @SuppressWarnings("unused")
    public static void receive(
            MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
            FriendlyByteBuf buf, PacketSender responseSender
    ) {
        ResourceKey<Level> key = buf.readResourceKey(Registry.DIMENSION_REGISTRY);
        BlockPos pos = buf.readBlockPos();
        double force = buf.readDouble();
        float speed = buf.readFloat();
        float accel = buf.readFloat();
        boolean finalize = buf.readBoolean();

        ServerLevel level = server.getLevel(key);
        if (level == null) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.c2s_request").withStyle(ChatFormatting.RED)
            );
            return;
        }

        if (!(level.getBlockEntity(pos) instanceof PropellerBlockEntity<?> prop)) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.not_propeller").withStyle(ChatFormatting.RED)
            );
            return;
        }

        if (prop.isUneditable()) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.not_editable").withStyle(ChatFormatting.RED)
            );
            return;
        }

        if (!prop.setForce(force)) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.max_force", force, prop.getMaxConfigForce())
                            .withStyle(ChatFormatting.RED)
            );
            return;
        }

        if (!prop.setMaxSpeed(speed)) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.max_speed", speed, prop.getMaxConfigSpeed())
                            .withStyle(ChatFormatting.RED)
            );
            return;
        }

        if (!prop.setAcceleration(accel)) {
            player.sendSystemMessage(
                    Component.translatable("error.vsutil.max_accel", accel, prop.getMaxConfigAcceleration())
                            .withStyle(ChatFormatting.RED)
            );
            return;
        }

        PropellerBlockEntity.removePhysics(level, pos);
        if (finalize) prop.setUneditable();
        prop.attachPhysics();

        player.sendSystemMessage(
                Component.translatable("chat.vsutil.prop_success").withStyle(ChatFormatting.GREEN)
        );
    }
}
