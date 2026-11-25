/** Osobnik zdrowy, nieodporny: jedyny stan podatny na zarażenie. */
public class HealthyState implements HealthState {
    @Override
    public void update(Person person) {
        // zdrowy osobnik zmienia stan tylko przez zarażenie (robi to Simulation)
    }

    @Override
    public StateType getType() {
        return StateType.HEALTHY;
    }

    @Override
    public boolean isSusceptible() {
        return true;
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
