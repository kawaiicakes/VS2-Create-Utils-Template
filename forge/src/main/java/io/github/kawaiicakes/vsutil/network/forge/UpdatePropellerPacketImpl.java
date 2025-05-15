package io.github.kawaiicakes.vsutil.network.forge;

import io.github.kawaiicakes.vsutil.network.UpdatePropellerPacket;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdatePropellerPacketImpl extends UpdatePropellerPacket {
    public static UpdatePropellerPacket create(
            ResourceKey<Level> level, BlockPos pos,
            double force, float maxSpeed, float accel, boolean finalize
    ) {
        return new UpdatePropellerPacketImpl(level, pos, force, maxSpeed, accel, finalize);
    }

    private final ResourceKey<Level> level;
    private final BlockPos pos;
    private final double force;
    private final float maxSpeed;
    private final float accel;
    private final boolean finalize;

    public UpdatePropellerPacketImpl(
            ResourceKey<Level> level, BlockPos pos,
            double force, float maxSpeed, float accel, boolean finalize
    ) {
        this.level = level;
        this.pos = pos;
        this.force = force;
        this.maxSpeed = maxSpeed;
        this.accel = accel;
        this.finalize = finalize;
    }

    public UpdatePropellerPacketImpl(FriendlyByteBuf buf) {
        this.level = buf.readResourceKey(Registries.DIMENSION);
        this.pos = buf.readBlockPos();
        this.force = buf.readDouble();
        this.maxSpeed = buf.readFloat();
        this.accel = buf.readFloat();
        this.finalize = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceKey(this.level);
        buf.writeBlockPos(this.pos);
        buf.writeDouble(this.force);
        buf.writeFloat(this.maxSpeed);
        buf.writeFloat(this.accel);
        buf.writeBoolean(this.finalize);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(
                () -> {
                    ServerPlayer player = context.getSender();
                    if (player == null) return;

                    ServerLevel level = player.server.getLevel(this.level);
                    if (level == null) {
                        player.sendSystemMessage(
                                Component.translatable("error.vsutil.c2s_request").withStyle(ChatFormatting.RED)
                        );
                        return;
                    }

                    if (!(level.getBlockEntity(this.pos) instanceof PropellerBlockEntity<?> prop)) {
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

                    if (!prop.setForce(this.force)) {
                        player.sendSystemMessage(
                                Component.translatable(
                                        "error.vsutil.max_force",
                                        this.force,
                                        prop.getMaxConfigForce()
                                ).withStyle(ChatFormatting.RED)
                        );
                        return;
                    }

                    if (!prop.setMaxSpeed(this.maxSpeed)) {
                        player.sendSystemMessage(
                                Component.translatable(
                                        "error.vsutil.max_speed",
                                        this.maxSpeed,
                                        prop.getMaxConfigSpeed()
                                ).withStyle(ChatFormatting.RED)
                        );
                        return;
                    }

                    if (!prop.setAcceleration(accel)) {
                        player.sendSystemMessage(
                                Component.translatable(
                                        "error.vsutil.max_accel",
                                        this.accel,
                                        prop.getMaxConfigAcceleration()
                                ).withStyle(ChatFormatting.RED)
                        );
                        return;
                    }

                    PropellerBlockEntity.removePhysics(level, this.pos);
                    if (finalize) prop.setUneditable();
                    prop.attachPhysics();

                    player.sendSystemMessage(
                            Component.translatable("chat.vsutil.prop_success").withStyle(ChatFormatting.GREEN)
                    );
                }
        );
    }
}
