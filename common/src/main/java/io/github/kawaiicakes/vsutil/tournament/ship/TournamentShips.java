package io.github.kawaiicakes.vsutil.tournament.ship;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.util.concurrent.AtomicDouble;
import io.github.kawaiicakes.vsutil.tournament.TickScheduler;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("deprecation")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class TournamentShips implements ShipForcesInducer {
    @JsonIgnore
    private TickScheduler.Ticking ticker = null;

    private String levelId = Level.OVERWORLD.location().toString();
    public final List<PropellerData> propellers = new CopyOnWriteArrayList<>();

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (!(physShip instanceof PhysShipImpl ship)) return;

        if (this.ticker == null)
            this.ticker = TickScheduler.INSTANCE.serverTickPerm(this::tick);

        this.propellers.forEach(
                prop -> {
                    if (!prop.touchingWater) return;

                    Vector3d tPos = new Vector3d(prop.pos.x, prop.pos.y, prop.pos.z)
                            .add(0.5, 0.5, 0.5)
                            .sub(ship.getTransform().getPositionInShip());
                    Vector3d tForce = ship.getTransform()
                            .getShipToWorld()
                            .transformDirection(prop.force, new Vector3d());

                    ship.applyInvariantForceToPos(tForce.mul(prop.speed.get()), tPos);
                }
        );
    }

    private void tick(MinecraftServer server) {
        Level level = server.getLevel(getDimensionKey(this.levelId));

        if (level == null) throw new AssertionError("Unable to get Level \"" + this.levelId + "\"!");

        this.propellers.forEach(
                prop -> {

                    BlockPos pos = shipToWorldBlock(
                            level,
                            new Vector3d(prop.pos.x, prop.pos.y, prop.pos.z)
                    );

                    if (pos == null) return;

                    prop.touchingWater = level.isWaterAt(pos);

                    BlockEntity unknownBe = level.getBlockEntity(VectorConversionsMCKt.toBlockPos(prop.pos));

                    if (!(unknownBe instanceof PropellerBlockEntity<?> be)) return;
                    prop.speed.set(be.speed);
                }
        );
    }

    private static ResourceKey<Level> getDimensionKey(String dimensionId) {
        String[] split = dimensionId.split(":");
        ResourceLocation location = new ResourceLocation(split[split.length - 2], split[split.length - 1]);
        return ResourceKey.create(Registries.DIMENSION, location);
    }

    public void addPropeller(Vector3i pos, Vector3d force) {
        this.propellers.add(new PropellerData(pos, force, new AtomicDouble(), false));
    }

    public void removePropeller(Vector3i pos) {
        this.propellers.removeIf(existingPos -> existingPos.pos.equals(pos));
    }

    public static BlockPos shipToWorldBlock(Level level, Vector3d pos) {
        LoadedShip shipCore = VSGameUtilsKt.getShipObjectManagingPos(level, pos);
        if (shipCore == null) return null;
        Vector3d transformed = shipCore
                .getShipToWorld()
                .transformPosition(pos);
        return new BlockPos((int) transformed.x, (int) transformed.y, (int) transformed.z);
    }

    @Nullable
    @SuppressWarnings("UnstableApiUsage")
    public static TournamentShips getOrCreate(ServerShip ship, String level) {
        if (ship == null) return null;

        if (ship.getAttachment(TournamentShips.class) != null)
            return ship.getAttachment(TournamentShips.class);

        TournamentShips toReturn = new TournamentShips();
        toReturn.levelId = level;
        ship.saveAttachment(TournamentShips.class, toReturn);

        return toReturn;
    }

    @Nullable
    public static TournamentShips getOrCreate(ServerShip ship) {
        if (ship == null) return null;

        return TournamentShips.getOrCreate(
                ship,
                ship.getChunkClaimDimension()
        );
    }

    @Nullable
    public static TournamentShips get(ServerLevel level, BlockPos pos) {
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos) == null
                ? VSGameUtilsKt.getShipObjectManagingPos(level, pos)
                : VSGameUtilsKt.getShipManagingPos(level, pos);

        return TournamentShips.getOrCreate(ship);
    }

    // Record not used because the fields must be mutable.
    // DO NOT get rid of unused fields; Jackson uses them for serialization
    @SuppressWarnings("unused")
    public static class PropellerData {
        public Vector3i pos;
        public Vector3d force;
        public AtomicDouble speed;
        public boolean touchingWater;

        public PropellerData() {}

        public PropellerData(Vector3i pos, Vector3d force, AtomicDouble speed, boolean touchingWater) {
            this.pos = pos;
            this.force = force;
            this.speed = speed;
            this.touchingWater = touchingWater;
        }

        public Vector3i getPos() {
            return this.pos;
        }

        public Vector3d getForce() {
            return this.force;
        }

        public AtomicDouble getSpeed() {
            return this.speed;
        }

        public boolean isTouchingWater() {
            return this.touchingWater;
        }

        @Override
        public String toString() {
            return getClass().getName() + "(pos=" + this.pos + ", force=" + this.force + ", speed=" + this.speed + ", touchingWater=" + this.touchingWater + ")";
        }
    }
}
