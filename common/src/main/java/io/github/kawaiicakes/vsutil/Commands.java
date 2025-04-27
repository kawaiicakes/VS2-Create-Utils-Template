package io.github.kawaiicakes.vsutil;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import io.github.kawaiicakes.vsutil.api.InteractLogic;
import io.github.kawaiicakes.vsutil.api.ShipifyLogic;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.command.ShipArgument;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.feature.command.VSCommandSource;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class Commands {
    public static final SimpleCommandExceptionType NO_PLAYERS = new SimpleCommandExceptionType(Component.translatable("permissions.vsutil.requires.player"));

    public static LiteralArgumentBuilder<CommandSourceStack> registerCommands(
            LiteralArgumentBuilder<CommandSourceStack> literalBuilder
    ) {
        return literalBuilder.requires((stack) -> stack.hasPermission(2)).then(
                literal("shipify").then(
                        argument("coordinates", BlockPosArgument.blockPos()).executes((context) -> {
                            try {
                                ServerLevel level = context.getSource().getLevel();
                                BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "coordinates");

                                ShipifyLogic.shipify(
                                        level, pos, 1.0, "",false, false, context.getSource().getPlayer()
                                );
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                            return 0;
                        }).then(
                                argument("static", BoolArgumentType.bool()).executes((context) -> {
                                    try {
                                        ServerLevel level = context.getSource().getLevel();
                                        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "coordinates");
                                        boolean isStatic = BoolArgumentType.getBool(context, "static");

                                        ShipifyLogic.shipify(
                                                level, pos, 1.0, "", isStatic, false, context.getSource().getPlayer()
                                        );
                                    } catch (Exception e) {
                                        if (!(e instanceof CommandRuntimeException))
                                            VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                        throw e;
                                    }
                                    return 0;
                                }).then(
                                argument("scale", DoubleArgumentType.doubleArg(ShipifyLogic.getMinScaling())).executes(
                                        (context -> {
                                            try {
                                                ServerLevel level = context.getSource().getLevel();
                                                BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "coordinates");
                                                boolean isStatic = BoolArgumentType.getBool(context, "static");
                                                double scale = DoubleArgumentType.getDouble(context, "scale");

                                                ShipifyLogic.shipify(
                                                        level, pos, scale, "", isStatic, false, context.getSource().getPlayer()
                                                );
                                            } catch (Exception e) {
                                                if (!(e instanceof CommandRuntimeException))
                                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                                throw e;
                                            }
                                            return 0;
                                        })
                                ).then(
                                argument("no-collide", BoolArgumentType.bool()).executes(context -> {
                                    try {
                                        ServerLevel level = context.getSource().getLevel();
                                        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "coordinates");
                                        boolean isStatic = BoolArgumentType.getBool(context, "static");
                                        double scale = DoubleArgumentType.getDouble(context, "scale");
                                        boolean noCollide = BoolArgumentType.getBool(context, "no-collide");

                                        ShipifyLogic.shipify(
                                                level, pos, scale, "", isStatic, noCollide, context.getSource().getPlayer()
                                        );
                                    } catch (Exception e) {
                                        if (!(e instanceof CommandRuntimeException))
                                            VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                        throw e;
                                    }
                                    return 0;
                                }).then(
                                argument("name", StringArgumentType.word()).executes(context -> {
                                    try {
                                        ServerLevel level = context.getSource().getLevel();
                                        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "coordinates");
                                        boolean isStatic = BoolArgumentType.getBool(context, "static");
                                        double scale = DoubleArgumentType.getDouble(context, "scale");
                                        boolean noCollide = BoolArgumentType.getBool(context, "no-collide");
                                        String slug = StringArgumentType.getString(context, "name");

                                        ShipifyLogic.shipify(
                                                level, pos, scale, slug, isStatic, noCollide, context.getSource().getPlayer()
                                        );
                                    } catch (Exception e) {
                                        if (!(e instanceof CommandRuntimeException))
                                            VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                        throw e;
                                    }
                                    return 0;
                                }))))))
        ).then(
                literal("resize").then(argument("ship", ShipArgument.Companion.ships()).then(argument(
                        "scale", DoubleArgumentType.doubleArg(ShipifyLogic.getMinScaling())
                ).executes(context -> {
                    try {
                        @SuppressWarnings({"RedundantCast", "unchecked"})
                        ServerShip ship = (ServerShip) ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                        VSGameUtilsKt.getVsCore().scaleShip(
                                VSGameUtilsKt.getShipObjectWorld(context.getSource().getLevel()),
                                ship,
                                DoubleArgumentType.getDouble(context, "scale")
                        );
                    } catch (Exception e) {
                        if (!(e instanceof CommandRuntimeException))
                            VSUtil.LOGGER.error("Exception while running shipify command!", e);
                        throw e;
                    }
                    return 0;
                })))
        ).then(
                literal("getid").then(argument("ship", ShipArgument.Companion.ships())
                        .executes(context -> {
                            try {
                                @SuppressWarnings({"RedundantCast", "unchecked"})
                                Ship ship = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                                ServerPlayer player = context.getSource().getPlayer();

                                long id = ship.getId();

                                if (player != null)
                                    player.sendSystemMessage(Component.translatable("chat.vsutil.getid", ship.getSlug(), id));

                                return 1;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        })
                )
        ).then(
                literal("getslug").then(argument("id", LongArgumentType.longArg())
                        .executes(context -> {
                            try {
                                long id = LongArgumentType.getLong(context, "id");
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();

                                ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(id);

                                Component displayToPlayer = ship != null
                                        ? Component.translatable("chat.vsutil.getslug", id, ship.getSlug())
                                        : Component.translatable("chat.vsutil.invalid_ship");

                                if (player != null)
                                    player.sendSystemMessage(displayToPlayer);

                                return 1;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        }))
        ).then(
                literal("disableCollisions").then(argument("ship", ShipArgument.Companion.ships())
                        .executes(context -> {
                            try {
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship ship = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();

                                DisabledCollisionData.add(ship.getId());

                                ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
                                QueryableShipData<ServerShip> allShips = shipObjectWorld.getAllShips();

                                for (Ship existing : allShips) {
                                    VSGameUtilsKt.getShipObjectWorld(level)
                                            .disableCollisionBetweenBodies(ship.getId(), existing.getId());
                                }

                                if (player != null)
                                    player.sendSystemMessage(Component.translatable("chat.vsutil.disable_col", ship.getSlug()));

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        }).then(argument("otherShip", ShipArgument.Companion.ships()).executes(context -> {
                            try {
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship ship = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship otherShip = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "otherShip");
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();

                                CollisionPairData.add(ship.getId(), otherShip.getId());

                                ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);

                                shipObjectWorld.disableCollisionBetweenBodies(ship.getId(), otherShip.getId());

                                if (player != null)
                                    player.sendSystemMessage(Component.translatable("chat.vsutil.disable_col_between", ship.getSlug(), otherShip.getSlug()));

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        })))
        ).then(
                literal("enableCollisions").then(argument("ship", ShipArgument.Companion.ships())
                        .executes(context -> {
                            try {
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship ship = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();

                                DisabledCollisionData.remove(ship.getId());

                                ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
                                QueryableShipData<ServerShip> allShips = shipObjectWorld.getAllShips();

                                for (Ship existing : allShips) {
                                    VSGameUtilsKt.getShipObjectWorld(level)
                                            .enableCollisionBetweenBodies(ship.getId(), existing.getId());
                                }

                                if (player != null)
                                    player.sendSystemMessage(Component.translatable("chat.vsutil.enable_col", ship.getSlug()));

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        }).then(argument("otherShip", ShipArgument.Companion.ships()).executes(context -> {
                            try {
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship ship = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "ship");
                                @SuppressWarnings({"unchecked", "RedundantCast"})
                                Ship otherShip = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "otherShip");
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();

                                CollisionPairData.remove(ship.getId(), otherShip.getId());

                                ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);

                                shipObjectWorld.enableCollisionBetweenBodies(ship.getId(), otherShip.getId());

                                if (player != null)
                                    player.sendSystemMessage(Component.translatable("chat.vsutil.enable_col_between", ship.getSlug(), otherShip.getSlug()));

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running shipify command!", e);
                                throw e;
                            }
                        })))
        ).then(
                literal("interact").then(argument("pos", BlockPosArgument.blockPos())
                        .executes(context -> {
                            // FIXME - Unreliable at getting phys bearings to start.
                            try {
                                ServerLevel level = context.getSource().getLevel();
                                ServerPlayer player = context.getSource().getPlayer();
                                BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

                                if (player != null) {
                                    player.sendSystemMessage(Component.translatable(
                                            "chat.vsutil.interact",
                                            Registry.BLOCK.getKey(level.getBlockState(pos).getBlock()),
                                            pos
                                    ));
                                    InteractLogic.interactWith(player, level, pos);
                                } else {
                                    ServerPlayer randomPlayer = context.getSource()
                                            .getServer()
                                            .getPlayerList()
                                            .getPlayers()
                                            .stream()
                                            .findAny()
                                            .orElseThrow(NO_PLAYERS::create);

                                    InteractLogic.interactWith(randomPlayer, level, pos);
                                }

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running command!", e);
                                throw e;
                            }
                        }))
        ).then(
                literal("renameCurrent").then(argument("name", StringArgumentType.word())
                        .executes(context -> {
                            try {
                                ServerLevel level = context.getSource().getLevel();
                                ServerShipWorldCore core = VSGameUtilsKt.getShipObjectWorld(level);

                                if (context.getSource().getPlayer() != null) {
                                    Vec3 below = context.getSource().getPosition().relative(Direction.DOWN, 0.1);
                                    AABB box = new AABB(context.getSource().getPosition(), below);

                                    byte count = 0;
                                    Ship toRename = null;
                                    for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, box)) {
                                        if (count == 1) throw new CommandRuntimeException(
                                                Component.translatable("argument.valkyrienskies.ship.multiple_found")
                                        );
                                        count++;
                                        toRename = ship;
                                    }
                                    if (toRename == null) throw new CommandRuntimeException(
                                            Component.translatable("argument.valkyrienskies.ship.no_found")
                                    );

                                    ServerShip ship = core.getAllShips().getById(toRename.getId());
                                    if (ship == null) throw new AssertionError();

                                    ship.setSlug(StringArgumentType.getString(context, "name"));
                                    context.getSource().getPlayer().sendSystemMessage(
                                            Component.translatable("chat.vsutil.successful_rename")
                                    );
                                } else {
                                    ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(
                                            level,
                                            VectorConversionsMCKt.toJOML(context.getSource().getPosition())
                                    );

                                    if (ship == null) throw new CommandRuntimeException(
                                            Component.translatable("argument.valkyrienskies.ship.no_found")
                                    );

                                    ship.setSlug(StringArgumentType.getString(context, "name"));
                                }

                                return 0;
                            } catch (Exception e) {
                                if (!(e instanceof CommandRuntimeException))
                                    VSUtil.LOGGER.error("Exception while running command!", e);
                                throw e;
                            }
                        }))
        );
        // TODO
        /*.then( THIS SHIT IS BORKED BC IT HASN'T BEEN IMPLEMENTED YET WTF
                literal("weld").then(argument("first", ShipArgument.Companion.ships())
                        .then(argument("second", ShipArgument.Companion.ships()).executes(
                                context -> {
                                    try {
                                        ServerLevel level = context.getSource().getLevel();

                                        @SuppressWarnings({"unchecked", "RedundantCast"})
                                        Ship ship0 = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "first");
                                        @SuppressWarnings({"unchecked", "RedundantCast"})
                                        Ship ship1 = ShipArgument.Companion.getShip(((CommandContext<? extends VSCommandSource>) (Object) context), "second");

                                        WeldLogic.weldShips(level, ship0.getId(), ship1.getId());

                                        return 0;
                                    } catch (Exception e) {
                                        if (!(e instanceof CommandRuntimeException))
                                            VSUtil.LOGGER.error("Exception while running command!", e);
                                        throw e;
                                    }
                        })))
        );*/
    }
}
