/** Osobnik odporny: nie zaraża się i nie zaraża innych. Stan końcowy. */
public class ImmuneState implements HealthState {
    @Override
    public void update(Person person) {
        // odporność jest trwała - nic się nie zmienia
    }

    @Override
    public StateType getType() {
        return StateType.IMMUNE;
    }

    @Override
    public boolean isSusceptible() {
        return false;
    }

    @Override
    public boolean isInfectious() {
        return false;
    }

    @Override
    public double transmissionProbability() {
        return 0.0;
    }

    @Override
    public int getRemainingSteps() {
        return 0;
    }
}
