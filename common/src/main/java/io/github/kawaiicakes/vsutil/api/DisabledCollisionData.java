package io.github.kawaiicakes.vsutil.api;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashSet;
import java.util.Set;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

/**
 * SPDX-License-Identifier: UNLICENSED
 * <br><br>
 * This class is largely the work of <a href="https://www.curseforge.com/members/endal/projects">Endal.</a>
 */
public class DisabledCollisionData extends SavedData {
    protected static DisabledCollisionData INSTANCE = null;

    public static DisabledCollisionData getInstance() {
        if (INSTANCE == null) INSTANCE = new DisabledCollisionData();
        return INSTANCE;
    }

    public static void add(long ship) {
        if (DisabledCollisionData.INSTANCE != null) {
            DisabledCollisionData.INSTANCE.ships.add(ship);
            DisabledCollisionData.INSTANCE.setDirty(true);
        }
    }

    public static void remove(long ship) {
        if (DisabledCollisionData.INSTANCE != null) {
            DisabledCollisionData.INSTANCE.ships.remove(ship);
            DisabledCollisionData.INSTANCE.setDirty(true);
        }
    }

    public static DisabledCollisionData load(CompoundTag nbt) {
        DisabledCollisionData collisionData = getInstance();
        long[] minArr = nbt.getLongArray("ships");

        for (long l : minArr) {
            collisionData.ships.add(l);
        }

        return collisionData;
    }

    public static void load(ServerLevel level) {
        DisabledCollisionData collisionData = level.getDataStorage().computeIfAbsent(
                DisabledCollisionData::load, DisabledCollisionData::getInstance, MOD_ID + "_disabled_collisions"
        );

        ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
        QueryableShipData<ServerShip> allShips = shipObjectWorld.getAllShips();

        for (long noCollided : collisionData.ships) {
            for (Ship ship : allShips) {
                VSGameUtilsKt.getShipObjectWorld(level).disableCollisionBetweenBodies(noCollided, ship.getId());
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putLongArray("ships", this.ships.stream().toList());
        return compoundTag;
    }

    protected final Set<Long> ships;

    public static Set<Long> getShips(ServerLevel level) {
        ImmutableSet.Builder<Long> builder = ImmutableSet.builder();

        DisabledCollisionData data = level.getDataStorage().computeIfAbsent(
                DisabledCollisionData::load, DisabledCollisionData::getInstance, MOD_ID + "_disabled_collisions"
        );

        for (Long ship : data.ships) {
            builder.add(ship);
        }

        return builder.build();
    }

    protected DisabledCollisionData() {
        this.ships = new HashSet<>();
    }
}
