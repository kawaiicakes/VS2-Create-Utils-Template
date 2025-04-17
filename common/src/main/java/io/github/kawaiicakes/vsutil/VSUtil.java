package io.github.kawaiicakes.vsutil;

import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

// TODO - Sexy wand stuff including animated sprites and sounds
// TODO - Fix vs commands not showing ships
// TODO - Shipify wand (creating ships with a predetermined slug with named wand, adjustable miniship creator?)
// TODO - Anti-gravity wand + command
// TODO - Anti-buoyancy wand + command
public class VSUtil {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "vsutil";

    public static void init() {

    }

    public static void initClient() {

    }

    @ExpectPlatform
    public static MinecraftServer getCurrentServer() {
        throw new AssertionError("VSUtil had a whoopsies while compiling! Please report this to the dev!");
    }
}
