package io.github.kawaiicakes.vsutil.api;

import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentOrientationConstraint;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WeldLogic {
    public static void weldShips(ServerLevel level, long ship0, long ship1) throws AssertionError {
        ServerShipWorldCore serverWorld = VSGameUtilsKt.getShipObjectWorld(level);

        if (!serverWorld.getAllShips().contains(ship0) || !serverWorld.getAllShips().contains(ship1))
            throw new AssertionError("One or more of the passed ships do not exist!");

        Ship ship0Object = serverWorld.getAllShips().getById(ship0);
        Ship ship1Object = serverWorld.getAllShips().getById(ship1);

        assert ship0Object != null && ship1Object != null;

        VSConstraint weld = new VSAttachmentOrientationConstraint(
                ship0, ship1,
                1E-10,
                ship0Object.getTransform().getPositionInWorld(), ship0Object.getTransform().getPositionInWorld(),
                1E10,
                ship0Object.getTransform().getShipToWorldRotation(), ship1Object.getTransform().getShipToWorldRotation(),
                1E10
        );

        if (serverWorld.createNewConstraint(weld) == null)
            throw new AssertionError("Unable to create constraint!");
    }
}
