/** Zarażony bez objawów: zaraża z prawdopodobieństwem 50%. */
public class AsymptomaticState extends InfectedState {
    public AsymptomaticState(int remainingSteps) {
        super(remainingSteps);
    }

    @Override
    public StateType getType() {
        return StateType.INFECTED_ASYMPTOMATIC;
    }

    @Override
    public double transmissionProbability() {
        return 0.5;
    }
}
