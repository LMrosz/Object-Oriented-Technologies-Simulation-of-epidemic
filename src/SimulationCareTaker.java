import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CareTaker - przechowuje pamiątki (punkty zapisu) w pamięci i w plikach,
 * ale nie zagląda do ich zawartości.
 */
public class SimulationCareTaker {
    private final Simulation originator;
    private final List<SimulationMemento> mementos = new ArrayList<>();

    public SimulationCareTaker(Simulation originator) {
        this.originator = originator;
    }

    /** Zapisuje bieżący stan symulacji; zwraca numer punktu zapisu. */
    public int create() {
        mementos.add(originator.createMemento());
        return mementos.size() - 1;
    }

    /** Przywraca stan zapisany pod danym numerem (można wielokrotnie). */
    public void restore(int number) {
        if (number < 0 || number >= mementos.size()) {
            throw new IllegalArgumentException("Brak punktu zapisu o numerze " + number);
        }
        originator.restore(mementos.get(number));
    }

    public int size() {
        return mementos.size();
    }

    /** Zapisuje bieżący stan symulacji do pliku. */
    public void saveToFile(Path path) throws IOException {
        try (OutputStream file = Files.newOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(originator.createMemento());
        }
    }

    /** Wczytuje stan z pliku i odtwarza go w symulacji; dodaje go też do listy punktów zapisu. */
    public void loadFromFile(Path path) throws IOException, ClassNotFoundException {
        try (InputStream file = Files.newInputStream(path);
             ObjectInputStream in = new ObjectInputStream(file)) {
            SimulationMemento memento = (SimulationMemento) in.readObject();
            mementos.add(memento);
            originator.restore(memento);
        }
    }
}
