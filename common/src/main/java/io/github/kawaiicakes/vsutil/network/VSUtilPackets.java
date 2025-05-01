package io.github.kawaiicakes.vsutil.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class VSUtilPackets {
    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <MSG> void sendToServer(MSG payload) {
        throw new AssertionError();
    }
}
