import java.io.Serializable;

/** Zapis jednej pary osobników w kontakcie: id (idA &lt; idB) i liczba kolejnych kroków bliskości. */
public record ContactEntry(int idA, int idB, int steps) implements Serializable {
}
