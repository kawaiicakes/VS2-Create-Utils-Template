package io.github.kawaiicakes.vsutil.fabric;

import net.minecraft.server.MinecraftServer;

public class VSUtilImpl {
    public static MinecraftServer SERVER;

    public static MinecraftServer getCurrentServer() {
        return SERVER;
    }
}
