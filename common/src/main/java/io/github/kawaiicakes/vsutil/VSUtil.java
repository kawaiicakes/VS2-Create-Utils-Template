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
import org.valkyrienskies.core.impl.config_impl.VSConfigClassImpl;
import org.valkyrienskies.core.impl.hooks.VSEvents;

// TODO - Test on both loaders, then this is ready for 1.0.0-beta release

// TODO (next release) - Sexy wand stuff including animated sprites and sounds
// TODO (next release) - Shipify wand (creating ships with a predetermined slug with named wand, adjustable miniship creator?)
// TODO (next release) - Anti-gravity wand + command
// TODO (future release) - constraint command + wand. for 1.19.2, implement welds... lol
/*
    BlockPos are sometimes not loaded on ships. wtf? It seems like this is a random error. I don't even know how to
    reproduce it, but I've made SpaceEye aware of the problem. It's probably best to ignore it for now
 */
public class VSUtil {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "vsutil";

    public static void init() {
        VSConfigClassImpl.Companion.registerConfig("vsutil", TournamentConfig.class);

        TournamentBlocks.INSTANCE.register();
        TournamentBlockEntities.INSTANCE.register();
        TournamentItems.INSTANCE.register();

        // Ensures the attachment actually ticks on initial save (i.e. without requiring a restart)
        //noinspection deprecation
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
