package io.github.kawaiicakes.vsutil.tournament.registry;

import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

public class CreativeTabs {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("itemGroup.vsutil.main_tab"))
                .icon(() -> new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(MOD_ID, "collision_wand"))))
                .displayItems(
                        (itemDisplayParameters, output) -> {
                            List<Item> items = new ArrayList<>();
                            TournamentItems.INSTANCE.ITEMS.forEach(item -> items.add(item.get()));

                            items.forEach(output::accept);
                        }
                )
                .build();
    }
}
