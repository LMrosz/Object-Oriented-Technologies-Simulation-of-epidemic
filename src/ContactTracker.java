import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zlicza, przez ile kolejnych kroków każda para osobników znajduje się w odległości
 * nie większej niż promień zakażenia. Wyjście pary poza promień zeruje licznik.
 */
public class ContactTracker {
    private Map<Long, Integer> contactSteps = new HashMap<>();

    private static long key(Person a, Person b) {
        return key(a.getId(), b.getId());
    }

    private static long key(int idA, int idB) {
        int lo = Math.min(idA, idB);
        int hi = Math.max(idA, idB);
        return ((long) lo << 32) | (hi & 0xFFFFFFFFL);
    }

    /** Aktualizuje liczniki dla wszystkich par; pary poza promieniem (i usunięci osobnicy) znikają. */
    public void track(List<Person> persons, double radius) {
        Map<Long, Integer> next = new HashMap<>();
        for (int i = 0; i < persons.size(); i++) {
            Person a = persons.get(i);
            for (int j = i + 1; j < persons.size(); j++) {
                Person b = persons.get(j);
                if (a.distanceTo(b) <= radius) {
                    long k = key(a, b);
                    next.put(k, contactSteps.getOrDefault(k, 0) + 1);
                }
            }
        }
        contactSteps = next;
    }

    public int getContactSteps(Person a, Person b) {
        return contactSteps.getOrDefault(key(a, b), 0);
    }

    /** Zeruje licznik pary (po nieudanej próbie zarażenia trzeba znów odczekać wymagany czas). */
    public void reset(Person a, Person b) {
        contactSteps.remove(key(a, b));
    }

    /** Ręczne ustawienie licznika (m.in. do testów i odtwarzania). */
    void set(int idA, int idB, int steps) {
        contactSteps.put(key(idA, idB), steps);
    }

    public List<ContactEntry> snapshot() {
        List<ContactEntry> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : contactSteps.entrySet()) {
            long k = e.getKey();
            result.add(new ContactEntry((int) (k >>> 32), (int) k, e.getValue()));
        }
        result.sort((p, q) -> p.idA() != q.idA() ? Integer.compare(p.idA(), q.idA()) : Integer.compare(p.idB(), q.idB()));
        return result;
    }

    public void restore(List<ContactEntry> entries) {
        contactSteps = new HashMap<>();
        for (ContactEntry e : entries) {
            contactSteps.put(key(e.idA(), e.idB()), e.steps());
        }
    }
}
