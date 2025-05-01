package io.github.kawaiicakes.vsutil.item;

import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NoCollisionWand extends Item {
    public static final String SHIP_NBT_KEY = "ship";

    public NoCollisionWand() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).tab(TournamentItems.INSTANCE.TAB));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        InteractionResult toReturn = super.useOn(pContext);

        Player player = pContext.getPlayer();

        if (player != null)
            pContext.getPlayer().getCooldowns().addCooldown(this, 4);

        if (!(pContext.getLevel() instanceof ServerLevel level) || level.isClientSide) return toReturn;

        BlockPos pos = pContext.getClickedPos();
        var ship = VSGameUtilsKt.getShipManagingPos(level, pos);

        CompoundTag nbt = pContext.getItemInHand().getOrCreateTag();

        if (ship == null) {
            if (nbt.contains(SHIP_NBT_KEY, Tag.TAG_LONG))
                nbt.remove(SHIP_NBT_KEY);

            if (player != null)
                player.displayClientMessage(
                        Component.translatable("chat.vsutil.invalid").withStyle(ChatFormatting.RED),
                        true
                );

            return toReturn;
        }

        if (!nbt.contains(SHIP_NBT_KEY, Tag.TAG_LONG) || nbt.getLong(SHIP_NBT_KEY) == ship.getId()) {
            nbt.putLong(SHIP_NBT_KEY, ship.getId());
            if (player != null)
                player.displayClientMessage(
                        Component.translatable("chat.vsutil.select").withStyle(ChatFormatting.YELLOW),
                        true
                );
            return toReturn;
        }

        long selectionId = nbt.getLong(SHIP_NBT_KEY);
        boolean isCrouching = player != null && player.isCrouching();

        ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
        QueryableShipData<ServerShip> allShips = shipObjectWorld.getAllShips();

        if (isCrouching) {
            if (allShips.contains(selectionId) && allShips.contains(ship.getId())) {
                VSGameUtilsKt.getShipObjectWorld(level).enableCollisionBetweenBodies(selectionId, ship.getId());
                CollisionPairData.remove(selectionId, ship.getId());

                player.displayClientMessage(
                        Component.translatable("chat.vsutil.enable").withStyle(ChatFormatting.GREEN),
                        true
                );
            } else {
                player.displayClientMessage(
                        Component.translatable("chat.vsutil.invalid").withStyle(ChatFormatting.RED),
                        true
                );
            }
        } else {
            if (allShips.contains(selectionId) && allShips.contains(ship.getId())) {
                VSGameUtilsKt.getShipObjectWorld(level).disableCollisionBetweenBodies(selectionId, ship.getId());
                CollisionPairData.add(selectionId, ship.getId());

                if (player != null)
                    player.displayClientMessage(
                            Component.translatable("chat.vsutil.disable").withStyle(ChatFormatting.GREEN),
                            true
                    );
            } else {
                if (player != null)
                    player.displayClientMessage(
                           Component.translatable("chat.vsutil.invalid").withStyle(ChatFormatting.RED),
                            true
                    );
            }
        }

        nbt.remove(SHIP_NBT_KEY);

        return toReturn;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced
    ) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        CompoundTag nbt = stack.getOrCreateTag();

        if (nbt.contains(SHIP_NBT_KEY, Tag.TAG_LONG)) {
            tooltipComponents.add(
                    Component.translatable("tooltip.vsutil.id", nbt.getLong(SHIP_NBT_KEY))
            );
        }
    }
}
