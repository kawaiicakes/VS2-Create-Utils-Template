package io.github.kawaiicakes.vsutil;

import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.kawaiicakes.vsutil.tournament.TournamentBlockEntities;
import io.github.kawaiicakes.vsutil.tournament.TournamentBlocks;
import io.github.kawaiicakes.vsutil.tournament.TournamentConfig;
import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import io.github.kawaiicakes.vsutil.tournament.ship.TournamentShips;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.core.impl.hooks.VSEvents;

// TODO - After this is tested, 1.0.0 is ready.

// FIXME (next release) - BlockPos are sometimes not loaded on ships. wtf? get around this while still checking for load
// TODO (next release) - Sexy wand stuff including animated sprites and sounds
// TODO (next release) - Fix vs commands not showing ships
// TODO (next release) - Shipify wand (creating ships with a predetermined slug with named wand, adjustable miniship creator?)
// TODO (next release) - /vsutil data command; comes with returns and intended for datapacks
// TODO (next release) - Anti-gravity wand + command
// TODO (next release) - Anti-buoyancy wand + command
public class VSUtil {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "vsutil";

    public static void init() {
        VSConfigClass.Companion.registerConfig("vsutil", TournamentConfig.class);

        TournamentBlocks.INSTANCE.register();
        TournamentBlockEntities.INSTANCE.register();
        TournamentItems.INSTANCE.register();

        // Ensures the attachment actually ticks on initial save (i.e. without requiring a restart)
        VSEvents.INSTANCE.getShipLoadEvent().on(
                event -> TournamentShips.getOrCreate(event.getShip())
        );
    }

    public static void initClient() {

    }

    public static void initClientRenderers(ClientRenderers clientRenderers) {
        TournamentBlockEntities.INSTANCE.initClientRenderers(clientRenderers);
    }

    public interface ClientRenderers {
        <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> t, BlockEntityRendererProvider<T> r);
    }

    @ExpectPlatform
    public static MinecraftServer getCurrentServer() {
        throw new AssertionError("VSUtil had a whoopsies while compiling! Please report this to the dev!");
    }
}
