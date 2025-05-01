package io.github.kawaiicakes.vsutil.tournament;

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object TournamentConfig {
    @JvmField
    val SERVER = Server()

    class Server {
        @JsonSchema(description = "The max force a big propeller can create")
        var propellerBigForce = 1000000.0;

        @JsonSchema(description = "The maximum achievable speed of a big propeller")
        var propellerBigSpeed = 700.0f;

        @JsonSchema(description = "The max acceleration of a big propeller.")
        var propellerBigAccel = 10f;

        @JsonSchema(description = "The default force of a big propeller at max speed")
        var propellerDefaultBigForce = 10000.0;

        @JsonSchema(description = "The default max speed of a big propeller")
        var propellerDefaultBigSpeed = 7.0F;

        @JsonSchema(description = "The default acceleration of a big propeller. (deaccel = accel * 2)")
        var propellerDefaultBigAccel = 0.1F;

        @JsonSchema(description = "The max force a small propeller can create")
        var propellerSmallForce = 100000.0;

        @JsonSchema(description = "The maximum achievable speed of a small propeller")
        var propellerSmallSpeed = 5000.0f;

        @JsonSchema(description = "The max acceleration of a small propeller.")
        var propellerSmallAccel = 100.0f;

        @JsonSchema(description = "The default force of a small propeller at max speed")
        var propellerDefaultSmallForce = 1000.0;

        @JsonSchema(description = "The default max speed of a small propeller")
        var propellerDefaultSmallSpeed = 5000.0F;

        @JsonSchema(description = "The default acceleration of a small propeller. (deaccel = accel * 2)")
        var propellerDefaultSmallAccel = 100.0F;
    }
}
