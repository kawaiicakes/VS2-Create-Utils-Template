package io.github.kawaiicakes.vsutil.tournament;

// FIXME - VSConfig doesn't seem to work. Redo this the old-fashioned way with side-specific implementations
public class TournamentConfig {
    public static final TournamentConfig INSTANCE = new TournamentConfig();

    public final Server SERVER = new Server();

    /**
     * This deviates from Tournament's config in that these values are the default maximums allowed; as propellers are
     * modified to have (in-game) configurable stats.
     */
    public static class Server {
        // original: 10000.0
        public double propellerBigForce = 1000000.0;

        // original: 7.0f
        public float propellerBigSpeed = 700.0f;

        // original 0.1f
        public float propellerBigAccel = 10f;

        public double propellerDefaultBigForce = 10000.0;

        public float propellerDefaultBigSpeed = 7.0F;

        public float propellerDefaultBigAccel = 0.1F;

        // original 1000.0
        public double propellerSmallForce = 100000.0;

        // original 50.0f
        public float propellerSmallSpeed = 5000.0f;

        // original 1.0f
        public float propellerSmallAccel = 100.0f;

        public double propellerDefaultSmallForce = 1000.0;

        public float propellerDefaultSmallSpeed = 5000.0F;

        public float propellerDefaultSmallAccel = 100.0F;
    }

    private TournamentConfig() {}
}
