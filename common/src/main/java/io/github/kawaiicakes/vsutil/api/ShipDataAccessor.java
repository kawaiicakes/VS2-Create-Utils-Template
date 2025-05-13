package io.github.kawaiicakes.vsutil.api;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.command.ShipArgument;
import org.valkyrienskies.mod.mixinducks.feature.command.VSCommandSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.function.Function;

// TODO (future release) - Accept and allow more entity NBT data and also arbitrary NBT data (in case somebody wants to do something creative)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShipDataAccessor implements DataAccessor {
    public static final Function<String, DataCommands.DataProvider> PROVIDER = s -> new DataCommands.DataProvider() {
        final String argumentType = s;

        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> context) {
            // noinspection unchecked,RedundantCast
            return new ShipDataAccessor(
                    ShipArgument.Companion.getShip(
                            ((CommandContext<? extends VSCommandSource>) (Object) context),
                            this.argumentType
                    ),
                    context
            );
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(
                ArgumentBuilder<CommandSourceStack, ?> builder,
                Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> action
        ) {
            return builder.then(
                    Commands.literal("ship")
                            .then(action.apply(
                                    Commands.argument(this.argumentType, ShipArgument.Companion.ships()))
                            )
            );
        }
    };

    private final ServerShip ship;
    private final CommandContext<CommandSourceStack> source;

    public ShipDataAccessor(Ship ship, CommandContext<CommandSourceStack> source) {
        this.ship = (ServerShip) ship;
        this.source = source;
    }

    @Override
    public void setData(CompoundTag other) {
        ListTag pos = other.getList("Pos", Tag.TAG_DOUBLE);
        ListTag motion = other.getList("Motion", Tag.TAG_DOUBLE);
        ListTag rot = other.getList("Rotation", Tag.TAG_FLOAT);
        ListTag omega = other.getList("Omega", Tag.TAG_DOUBLE);

        /*
            Vector3dc newPos,
            Quaterniondc newRot,
            Vector3dc newVel,
            Vector3dc newOmega,
            String newDimension,
            Double newScale

            /data modify ship BALL_SUCKER Pos merge from entity Dev Pos
         */
        VSGameUtilsKt.getVsCore().teleportShip(
                VSGameUtilsKt.getShipObjectWorld(this.source.getSource().getLevel()),
                this.ship,
                new ShipTeleportDataImpl(
                        vecFromList(pos),
                        quaternionFromList(rot),
                        vecFromList(motion),
                        vecFromList(omega),
                        this.ship.getChunkClaimDimension(),
                        // arbitrarily chosen component; the value should almost always be the same for each component
                        this.ship.getTransform().getShipToWorldScaling().x()
                )
        );
    }

    @Override
    public CompoundTag getData() {
        CompoundTag toReturn = new CompoundTag();

        toReturn.put("Pos", toDouble(GetPosAngleLogic.getPos(this.ship)));
        toReturn.put("Motion", toDouble(GetPosAngleLogic.getMotion(this.ship)));
        toReturn.put("Rotation", toFloat(GetPosAngleLogic.getEuler(this.ship)));
        toReturn.put("Omega", toDouble(GetPosAngleLogic.getOmega(this.ship)));

        return toReturn;
    }

    @Override
    public Component getModifiedSuccess() {
        return Component.translatable("commands.vsutil.data.ship.modified", this.ship.getSlug());
    }

    @Override
    public Component getPrintSuccess(Tag nbt) {
        return Component.translatable(
                "commands.vsutil.data.ship.query",
                this.ship.getSlug(),
                NbtUtils.toPrettyComponent(nbt)
        );
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath path, double scale, int value) {
        return Component.translatable(
                "commands.data.entity.get",
                path,
                this.ship.getSlug(),
                String.format(Locale.ROOT, "%.2f", scale),
                value
        );
    }

    public static ListTag toDouble(Vec3 source) {
        ListTag toReturn = new ListTag();
        toReturn.add(DoubleTag.valueOf(source.x()));
        toReturn.add(DoubleTag.valueOf(source.y()));
        toReturn.add(DoubleTag.valueOf(source.z()));
        return toReturn;
    }

    public static ListTag toFloat(Vec3 source) {
        ListTag toReturn = new ListTag();
        toReturn.add(FloatTag.valueOf((float) source.x()));
        toReturn.add(FloatTag.valueOf((float) source.y()));
        toReturn.add(FloatTag.valueOf((float) source.z()));
        return toReturn;
    }

    public static Vector3dc vecFromList(ListTag tag) {
        return new Vector3d(tag.getDouble(0), tag.getDouble(1), tag.getDouble(2));
    }

    /**
     * <a href="https://en.wikipedia.org/w/index.php?title=Conversion_between_quaternions_and_Euler_angles&oldid=1275647430#Source_code">Wikipedia code goes hard lmao</a>
     */
    public static Quaterniondc quaternionFromList(ListTag tag) {
        double pitch = Math.toRadians(tag.getFloat(1));
        double yaw = Math.toRadians(-tag.getFloat(0));
        double roll = Math.toRadians(tag.getFloat(2));

        double cr = Math.cos(pitch * 0.5);
        double sr = Math.sin(pitch * 0.5);
        double cp = Math.cos(yaw * 0.5);
        double sp = Math.sin(yaw * 0.5);
        double cy = Math.cos(roll * 0.5);
        double sy = Math.sin(roll * 0.5);

        return new Quaterniond(
                sr * cp * cy - cr * sp * sy,
                cr * sp * cy + sr * cp * sy,
                cr * cp * sy - sr * sp * cy,
                cr * cp * cy + sr * sp * sy
        );
    }
}
