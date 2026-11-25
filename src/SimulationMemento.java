import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Memento - pełny, niezmienny zapis stanu symulacji w kroku t.
 * Zawartość jest widoczna tylko dla Simulation (gettery pakietowe), CareTaker jedynie ją przechowuje.
 * Zapisany jest też generator liczb losowych, więc dalszy przebieg po odtworzeniu jest identyczny.
 */
public class SimulationMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int stepNumber;
    private final List<PersonSnapshot> persons;
    private final List<ContactEntry> contacts;
    private final Random random;
    private final int nextId;
    private final SimulationConfig config;

    SimulationMemento(int stepNumber, List<PersonSnapshot> persons, List<ContactEntry> contacts,
                      Random random, int nextId, SimulationConfig config) {
        this.stepNumber = stepNumber;
        this.persons = new ArrayList<>(persons);
        this.contacts = new ArrayList<>(contacts);
        this.random = copyOf(random);
        this.nextId = nextId;
        this.config = config;
    }

    int getStepNumber() {
        return stepNumber;
    }

    List<PersonSnapshot> getPersons() {
        return persons;
    }

    List<ContactEntry> getContacts() {
        return contacts;
    }

    /** Zwraca kopię generatora, żeby jedna pamiątka dała się odtworzyć wielokrotnie. */
    Random getRandom() {
        return copyOf(random);
    }

    int getNextId() {
        return nextId;
    }

    SimulationConfig getConfig() {
        return config;
    }

    /** Głęboka kopia java.util.Random (jest Serializable) wraz z całym wewnętrznym stanem. */
    static Random copyOf(Random source) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeObject(source);
            }
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                return (Random) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Nie udało się skopiować generatora liczb losowych", e);
        }
    }
}
