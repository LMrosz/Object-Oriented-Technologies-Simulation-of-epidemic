public class SimulationConfig {
    private final double areaWidth;
    private final double areaHeight;
    private final double maxSpeed;
    private final int stepsPerSecond;
    private final double borderExitProb;
    private final double entryInfectionProb;
    private final double infectionRadius;
    private final int requiredContactSteps;
    private final int infectionMinSteps;
    private final int infectionMaxSteps;
    private final int initialPopulation;
    private final double spawnRate;
    private final double immuneRatio;

    // Wartości z polecenia. immuneRatio = 0,1 (przypadek 2); przypadek 1 to immuneRatio = 0.
    // spawnRate = 0,01 to wartość startowa (szansa na krok) do dobrania doświadczalnie.
    public SimulationConfig() {
        this(100, 100, 2.5, 25, 0.5, 0.1, 2.0, 75, 500, 750, 100, 0.01, 0.1);
    }

    public SimulationConfig(double areaWidth, double areaHeight, double maxSpeed, int stepsPerSecond,
                            double borderExitProb, double entryInfectionProb, double infectionRadius,
                            int requiredContactSteps, int infectionMinSteps, int infectionMaxSteps,
                            int initialPopulation, double spawnRate, double immuneRatio) {
        if (areaWidth <= 0 || areaHeight <= 0) {
            throw new IllegalArgumentException("Wymiary obszaru muszą być dodatnie");
        }
        if (maxSpeed <= 0) {
            throw new IllegalArgumentException("maxSpeed musi być dodatni");
        }
        if (stepsPerSecond <= 0) {
            throw new IllegalArgumentException("stepsPerSecond musi być dodatni");
        }
        requireProbability(borderExitProb, "borderExitProb");
        requireProbability(entryInfectionProb, "entryInfectionProb");
        requireProbability(spawnRate, "spawnRate");
        requireProbability(immuneRatio, "immuneRatio");
        if (infectionRadius <= 0) {
            throw new IllegalArgumentException("infectionRadius musi być dodatni");
        }
        if (requiredContactSteps < 1) {
            throw new IllegalArgumentException("requiredContactSteps musi być >= 1");
        }
        if (infectionMinSteps < 1 || infectionMinSteps > infectionMaxSteps) {
            throw new IllegalArgumentException("Czas choroby: 1 <= infectionMinSteps <= infectionMaxSteps");
        }
        if (initialPopulation < 0) {
            throw new IllegalArgumentException("initialPopulation nie może być ujemna");
        }

        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        this.maxSpeed = maxSpeed;
        this.stepsPerSecond = stepsPerSecond;
        this.borderExitProb = borderExitProb;
        this.entryInfectionProb = entryInfectionProb;
        this.infectionRadius = infectionRadius;
        this.requiredContactSteps = requiredContactSteps;
        this.infectionMinSteps = infectionMinSteps;
        this.infectionMaxSteps = infectionMaxSteps;
        this.initialPopulation = initialPopulation;
        this.spawnRate = spawnRate;
        this.immuneRatio = immuneRatio;
    }

    private static void requireProbability(double p, String name) {
        if (!(p >= 0 && p <= 1)) {
            throw new IllegalArgumentException(name + " musi być w przedziale [0, 1]");
        }
    }

    public double getAreaWidth() {
        return areaWidth;
    }

    public double getAreaHeight() {
        return areaHeight;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public int getStepsPerSecond() {
        return stepsPerSecond;
    }

    public double getBorderExitProb() {
        return borderExitProb;
    }

    public double getEntryInfectionProb() {
        return entryInfectionProb;
    }

    public double getInfectionRadius() {
        return infectionRadius;
    }

    public int getRequiredContactSteps() {
        return requiredContactSteps;
    }

    public int getInfectionMinSteps() {
        return infectionMinSteps;
    }

    public int getInfectionMaxSteps() {
        return infectionMaxSteps;
    }

    public int getInitialPopulation() {
        return initialPopulation;
    }

    public double getSpawnRate() {
        return spawnRate;
    }

    public double getImmuneRatio() {
        return immuneRatio;
    }
}
