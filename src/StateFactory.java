import java.util.Random;

/** Fabryka stanów zdrowotnych - jedyne miejsce, które zna wszystkie klasy konkretnych stanów. */
public final class StateFactory {
    private StateFactory() {
    }

    /** Tworzy stan danego typu; remainingSteps ma znaczenie tylko dla stanów zarażenia. */
    public static HealthState create(StateType type, int remainingSteps) {
        switch (type) {
            case IMMUNE:
                return new ImmuneState();
            case HEALTHY:
                return new HealthyState();
            case INFECTED_SYMPTOMATIC:
                return new SymptomaticState(remainingSteps);
            case INFECTED_ASYMPTOMATIC:
                return new AsymptomaticState(remainingSteps);
            default:
                throw new IllegalArgumentException("Nieznany typ stanu: " + type);
        }
    }

    /**
     * Losuje nowe zarażenie: z objawami lub bez (50/50) oraz czas choroby
     * z przedziału [infectionMinSteps, infectionMaxSteps] (20-30 s symulacji).
     */
    public static InfectedState randomInfected(Random random, SimulationConfig config) {
        int range = config.getInfectionMaxSteps() - config.getInfectionMinSteps() + 1;
        int steps = config.getInfectionMinSteps() + random.nextInt(range);
        if (random.nextBoolean()) {
            return new SymptomaticState(steps);
        }
        return new AsymptomaticState(steps);
    }
}
