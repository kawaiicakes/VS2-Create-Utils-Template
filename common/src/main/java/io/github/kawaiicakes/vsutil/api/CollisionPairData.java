package io.github.kawaiicakes.vsutil.api;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

/**
 * SPDX-License-Identifier: UNLICENSED
 * <br><br>
 * This class is largely the work of <a href="https://www.curseforge.com/members/endal/projects">Endal.</a>
 */
public class CollisionPairData extends SavedData {
    private static CollisionPairData INSTANCE;
    private final Set<Pair<Long, Long>> noCollisionPairs = new HashSet<>();
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        final int numberOfPairs = noCollisionPairs.size();

        long[] min = new long[numberOfPairs];
        long[] max = new long[numberOfPairs];

        int pairIndex = 0;
        for (Pair<Long, Long> pair : this.noCollisionPairs) {
            min[pairIndex] = pair.first();
            max[pairIndex] = pair.second();
            pairIndex++;
        }

        compoundTag.putLongArray("min", min);
        compoundTag.putLongArray("max", max);

        return compoundTag;
    }

    public static CollisionPairData create() {
        return new CollisionPairData();
    }

    public static void add(long ship0, long ship1) {
        if (CollisionPairData.INSTANCE != null) {
            CollisionPairData.INSTANCE.noCollisionPairs.add(orderedPair(ship0, ship1));
            CollisionPairData.INSTANCE.setDirty(true);
        }
    }

    public static void remove(long ship0, long ship1) {
        if (CollisionPairData.INSTANCE != null) {
            CollisionPairData.INSTANCE.noCollisionPairs.remove(orderedPair(ship0, ship1));
            CollisionPairData.INSTANCE.setDirty(true);
        }
    }

    public static CollisionPairData load(CompoundTag nbt) {
        CollisionPairData pairData = create();
        long[] minArr = nbt.getLongArray("min");
        long[] maxArr = nbt.getLongArray("max");

        for (int i = 0; i < minArr.length; i++) {
            pairData.noCollisionPairs.add(Pair.of(minArr[i], maxArr[i]));
        }

        return pairData;
    }

    public static void load(ServerLevel level) {
        CollisionPairData pairData = level.getDataStorage().computeIfAbsent(
                CollisionPairData::load, CollisionPairData::create, MOD_ID + "_collision_pairs"
        );

        ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
        QueryableShipData<ServerShip> allShips = shipObjectWorld.getAllShips();

        Set<Long> existing = allShips.stream().map(ServerShip::getId).collect(Collectors.toSet());
        Set<Pair<Long, Long>> invalid = new HashSet<>();

        // These are three separate blocks since I'm not sure if this would cause random ConcurrentModificationExceptions
        for (Pair<Long, Long> pair : pairData.noCollisionPairs) {
            if (!existing.contains(pair.first()) || !existing.contains(pair.second()))
                // Ensure shallow copy
                invalid.add(Pair.of(pair.first(), pair.second()));
        }

        invalid.forEach(pair -> {
            if (!pairData.noCollisionPairs.contains(pair)) return;
            pairData.noCollisionPairs.remove(pair);
        });

        for (Pair<Long, Long> pair : pairData.noCollisionPairs) {
            shipObjectWorld.disableCollisionBetweenBodies(
                    pair.first(), pair.second()
            );
        }

        CollisionPairData.INSTANCE = pairData;
    }

    public static Pair<Long, Long> orderedPair(long ship0, long ship1) {
        return Pair.of(Math.min(ship0, ship1), Math.max(ship0, ship1));
    }
}
