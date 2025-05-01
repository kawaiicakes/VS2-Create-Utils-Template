package io.github.kawaiicakes.vsutil.tournament.blockentity;

import io.github.kawaiicakes.vsutil.tournament.TournamentBlockEntities;
import io.github.kawaiicakes.vsutil.tournament.TournamentConfig;
import io.github.kawaiicakes.vsutil.tournament.block.PropellerBlock;
import io.github.kawaiicakes.vsutil.tournament.ship.TournamentShips;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

@MethodsReturnNonnullByDefault
public abstract class PropellerBlockEntity<T extends BlockEntity> extends BlockEntity {
    public static final BlockEntityTicker<PropellerBlockEntity<?>> TICKER =
            (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level);

    private boolean editable = true;
    private double force;
    private float maxSpeed;
    private float accel;
    public int signal = -1;
    public double rotation = 0.0;
    public double speed = 0.0;

    public PropellerBlockEntity(
            BlockEntityType<?> type, BlockPos pos, BlockState blockState, double force, float maxSpeed, float accel
    ) {
        super(type, pos, blockState);
        this.force = force;
        this.maxSpeed = maxSpeed;
        this.accel = accel;
    }

    // TODO - does setChanged() need to be called in here to save propeller's (current) speed across restarts?
    //  Saving seems kinda inconsistent...
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

    public boolean setForce(double force) {
        if (!this.editable || force > this.getMaxConfigForce()) return false;
        this.force = force;
        this.update();
        this.setChanged();
        return true;
    }

    public boolean setMaxSpeed(float maxSpeed) {
        if (!this.editable || maxSpeed > this.getMaxConfigSpeed()) return false;
        this.maxSpeed = maxSpeed;
        this.update();
        this.setChanged();
        return true;
    }

    public boolean setAcceleration(float acceleration) {
        if (!this.editable || acceleration > this.getMaxConfigAcceleration()) return false;
        this.accel = acceleration;
        this.update();
        this.setChanged();
        return true;
    }

    public void setUneditable() {
        this.editable = false;
        this.update();
        this.setChanged();
    }

    public boolean isUneditable() {
        return !this.editable;
    }

    public double getForce() {
        return this.force;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public float getAcceleration() {
        return this.accel;
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
        tag.putBoolean("editable", this.editable);
        tag.putDouble("force", this.force);
        tag.putFloat("maxSpeed", this.maxSpeed);
        tag.putFloat("accel", this.accel);
        tag.putDouble("speed", this.speed);
        tag.putDouble("rotation", this.rotation);
        tag.putInt("signal", this.signal);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.editable = tag.getBoolean("editable");
        this.force = tag.getDouble("force");
        this.maxSpeed = tag.getFloat("maxSpeed");
        this.accel = tag.getFloat("accel");
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

    public void attachPhysics() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        TournamentShips instance = TournamentShips.get(serverLevel, this.getBlockPos());
        if (instance != null)
            instance.addPropeller(
                    VectorConversionsMCKt.toJOML(this.getBlockPos()),
                    VectorConversionsMCKt.toJOMLD(this.getBlockState().getValue(FACING).getNormal()).mul(this.force)
            );
    }

    // Static as this is expected to execute even if the instances of this no longer exist/cannot be referenced
    public static void removePhysics(ServerLevel serverLevel, BlockPos pos) {
        TournamentShips instance = TournamentShips.get(serverLevel, pos);
        if (instance != null)
            instance.removePropeller(
                    VectorConversionsMCKt.toJOML(pos)
            );
    }

    public abstract double getMaxConfigForce();

    public abstract float getMaxConfigSpeed();

    public abstract float getMaxConfigAcceleration();

    public static class BigPropellerBlockEntity extends PropellerBlockEntity<BigPropellerBlockEntity> {
        public BigPropellerBlockEntity(BlockPos pos, BlockState state) {
            super(
                    TournamentBlockEntities.INSTANCE.PROP_BIG.get(),
                    pos,
                    state,
                    TournamentConfig.SERVER.getPropellerDefaultBigForce(),
                    TournamentConfig.SERVER.getPropellerDefaultBigSpeed(),
                    TournamentConfig.SERVER.getPropellerDefaultBigAccel()
            );
        }

        @Override
        public double getMaxConfigForce() {
            return TournamentConfig.SERVER.getPropellerBigForce();
        }

        @Override
        public float getMaxConfigSpeed() {
            return TournamentConfig.SERVER.getPropellerBigSpeed();
        }

        @Override
        public float getMaxConfigAcceleration() {
            return TournamentConfig.SERVER.getPropellerBigAccel();
        }
    }

    public static class SmallPropellerBlockEntity extends PropellerBlockEntity<SmallPropellerBlockEntity> {
        public SmallPropellerBlockEntity(BlockPos pos, BlockState state) {
            super(
                    TournamentBlockEntities.INSTANCE.PROP_SMALL.get(),
                    pos,
                    state,
                    TournamentConfig.SERVER.getPropellerDefaultSmallForce(),
                    TournamentConfig.SERVER.getPropellerDefaultSmallSpeed(),
                    TournamentConfig.SERVER.getPropellerDefaultSmallAccel()
            );
        }

        @Override
        public double getMaxConfigForce() {
            return TournamentConfig.SERVER.getPropellerSmallForce();
        }

        @Override
        public float getMaxConfigSpeed() {
            return TournamentConfig.SERVER.getPropellerSmallSpeed();
        }

        @Override
        public float getMaxConfigAcceleration() {
            return TournamentConfig.SERVER.getPropellerSmallAccel();
        }
    }
}
