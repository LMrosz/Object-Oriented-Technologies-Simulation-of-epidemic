/**
 * Typ stanu zdrowotnego osobnika. Używany m.in. do zapisu stanu w pamiątce
 * (Memento) oraz do zliczania osobników w interfejsie.
 */
public enum StateType {
    IMMUNE,
    HEALTHY,
    INFECTED_SYMPTOMATIC,
    INFECTED_ASYMPTOMATIC
}
