package io.github.kawaiicakes.vsutil.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kawaiicakes.vsutil.api.ShipDataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;

@Mixin(DataCommands.class)
public abstract class DataCommandsMixin {
    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/commands/data/DataCommands;ALL_PROVIDERS:Ljava/util/List;",
                    opcode = Opcodes.PUTSTATIC
            )
    )
    private static void addShipDataAccessor(
            List<Function<String, DataCommands.DataProvider>> value, Operation<Void> original
    ) {
        original.call(ImmutableList.builder().addAll(value).add(ShipDataAccessor.PROVIDER).build());
    }
}
