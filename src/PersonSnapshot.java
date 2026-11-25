import java.io.Serializable;

/**
 * Niezmienny zapis osobnika w pamiątce. Prędkość zapisana jest jako składowe (vx, vy),
 * aby odtworzenie było bitowo identyczne (przeliczanie kąt/długość mogłoby zmienić ostatni bit).
 */
public record PersonSnapshot(int id, double x, double y, double vx, double vy,
                             StateType stateType, int remainingSteps) implements Serializable {
}
