package io.github.kawaiicakes.vsutil.tournament.blockentity;

import io.github.kawaiicakes.vsutil.tournament.TournamentBlockEntities;
import io.github.kawaiicakes.vsutil.tournament.block.PropellerBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public class PropellerBlockEntity<T extends BlockEntity> extends BlockEntity {
    public static final BlockEntityTicker<PropellerBlockEntity<?>> TICKER =
            (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level);

    private final float maxSpeed;
    private final float accel;
    public int signal = -1;
    public double rotation = 0.0;
    public double speed = 0.0;

    public PropellerBlockEntity(
            BlockEntityType<?> type, BlockPos pos, BlockState blockState, float maxSpeed, float accel
    ) {
        super(type, pos, blockState);
        this.maxSpeed = maxSpeed;
        this.accel = accel;
    }

    private void tick(Level level) {
        if (this.signal == -1)
            this.signal = PropellerBlock.getPropSignal(this.getBlockState(), level, this.getBlockPos());

        float targetSpeed = this.signal / 15.0f * this.maxSpeed;

        if (this.speed < targetSpeed) {
            this.speed += this.accel;
        } else if (this.speed > targetSpeed) {
            this.speed -= this.accel * 2;
        }

        if (this.speed < 0.0f) {
            this.speed = 0.0;
        }

        this.rotation -= this.speed;
        this.rotation %= 360.0;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag toReturn = new CompoundTag();
        this.saveAdditional(toReturn);
        return toReturn;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putDouble("speed", this.speed);
        tag.putDouble("rotation", this.rotation);
        tag.putInt("signal", this.signal);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.speed = tag.getDouble("speed");
        this.rotation = tag.getDouble("rotation");
        this.signal = tag.getInt("signal");

        super.load(tag);
    }

    public void update() {
        if (this.level == null) throw new IllegalStateException();
        this.level.sendBlockUpdated(
                this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL_IMMEDIATE
        );
    }

    public static class BigPropellerBlockEntity extends PropellerBlockEntity<BigPropellerBlockEntity> {
        public BigPropellerBlockEntity(BlockPos pos, BlockState state) {
            super(
                    TournamentBlockEntities.INSTANCE.PROP_BIG.get(),
                    pos,
                    state,
                    7.0F,
                    0.1F
            );
        }
    }

    public static class SmallPropellerBlockEntity extends PropellerBlockEntity<SmallPropellerBlockEntity> {
        public SmallPropellerBlockEntity(BlockPos pos, BlockState state) {
            super(
                    TournamentBlockEntities.INSTANCE.PROP_SMALL.get(),
                    pos,
                    state,
                    50.0F,
                    1.0F
            );
        }
    }
}
