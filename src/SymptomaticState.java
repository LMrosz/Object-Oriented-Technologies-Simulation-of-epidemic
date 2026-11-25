/** Zarażony z objawami: zaraża z prawdopodobieństwem 100%. */
public class SymptomaticState extends InfectedState {
    public SymptomaticState(int remainingSteps) {
        super(remainingSteps);
    }

    @Override
    public StateType getType() {
        return StateType.INFECTED_SYMPTOMATIC;
    }

    @Override
    public double transmissionProbability() {
        return 1.0;
    }
}
