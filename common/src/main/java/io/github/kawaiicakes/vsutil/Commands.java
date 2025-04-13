package io.github.kawaiicakes.vsutil;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import io.github.kawaiicakes.vsutil.api.ShipifyLogic;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class Commands {
    public static Logger LOGGER = LogUtils.getLogger();

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
                                    LOGGER.error("Exception while running shipify command!", e);
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
                                            LOGGER.error("Exception while running shipify command!", e);
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
                                                    LOGGER.error("Exception while running shipify command!", e);
                                                throw e;
                                            }
                                            return 0;
                                        })
                                ).then(
                                        // FIXME: this didn't work
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
                                            LOGGER.error("Exception while running shipify command!", e);
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
                                            LOGGER.error("Exception while running shipify command!", e);
                                        throw e;
                                    }
                                    return 0;
                                }))))) // FIXME: GENERIC TYPE CASTING
        )/*.then(
                literal("resize").then(argument("ship", ShipArgument.Companion.ships()).then(argument(
                        "scale", DoubleArgumentType.doubleArg(ShipifyLogic.getMinScaling())
                ).executes(context -> {
                    try {
                        ServerShip ship = (ServerShip) ShipArgument.Companion.getShip(context, "ship");
                        VSGameUtilsKt.getVsCore().scaleShip(
                                VSGameUtilsKt.getShipObjectWorld(context.getSource().getLevel()),
                                ship,
                                DoubleArgumentType.getDouble(context, "scale")
                        );
                    } catch (Exception e) {
                        if (!(e instanceof CommandRuntimeException))
                            LOGGER.error("Exception while running shipify command!", e);
                        throw e;
                    }
                    return 0;
                }))))
                */

        );
    }
}
