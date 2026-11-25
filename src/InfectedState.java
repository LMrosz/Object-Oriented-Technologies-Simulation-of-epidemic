/**
 * Wspólna część stanów osobnika zarażonego: odliczanie czasu choroby.
 * Po wyzdrowieniu osobnik staje się odporny (ImmuneState).
 */
public abstract class InfectedState implements HealthState {
    protected int remainingSteps;

    protected InfectedState(int remainingSteps) {
        if (remainingSteps < 0) throw new IllegalArgumentException("Liczba kroków nie może być ujemna");
        this.remainingSteps = remainingSteps;
    }

    @Override
    public void update(Person person) {
        remainingSteps--;
        if (remainingSteps <= 0) {
            person.setState(new ImmuneState());
        }
    }

    @Override
    public boolean isSusceptible() {
        return false;
    }

    @Override
    public boolean isInfectious() {
        return true;
    }

    @Override
    public int getRemainingSteps() {
        return remainingSteps;
    }
}
