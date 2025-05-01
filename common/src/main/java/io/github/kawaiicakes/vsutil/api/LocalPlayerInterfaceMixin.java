package io.github.kawaiicakes.vsutil.api;

import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;

// THIS CLASS MUST LOAD ON SERVER AND CLIENT
public interface LocalPlayerInterfaceMixin {
    default void vsutil$openPropeller(PropellerBlockEntity<?> propeller) {
    }
}
