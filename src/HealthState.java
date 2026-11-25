/**
 * State (wzorzec Stan) - wspólny interfejs stanów zdrowotnych osobnika.
 * Osobnik (Person) pełni rolę kontekstu i deleguje do aktualnego stanu
 * wszystko, co zależy od zdrowia.
 */
public interface HealthState {
    /** Wywoływane raz na krok symulacji; stan sam decyduje o przejściu do następnego stanu. */
    void update(Person person);

    StateType getType();

    /** Czy osobnik może się zarazić (zdrowy i nieodporny). */
    boolean isSusceptible();

    /** Czy osobnik zaraża innych. */
    boolean isInfectious();

    /** Prawdopodobieństwo zarażenia podatnego osobnika po spełnieniu warunków kontaktu. */
    double transmissionProbability();

    /** Liczba pozostałych kroków choroby (0 dla stanów, które nie są chorobą). */
    int getRemainingSteps();
}
