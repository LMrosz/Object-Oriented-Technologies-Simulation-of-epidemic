import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Symulacja zakażeń - Originator we wzorcu Pamiątka (tworzy i odtwarza SimulationMemento).
 * Jeden krok = 1/25 s symulacji.
 */
public class Simulation {
    private final List<Person> persons = new ArrayList<>();
    private Area area;
    private SimulationConfig config;
    private final ContactTracker tracker = new ContactTracker();
    private Random random;
    private int stepNumber;
    private int nextId;

    public Simulation(SimulationConfig config) {
        this(config, new Random());
    }

    public Simulation(SimulationConfig config, long seed) {
        this(config, new Random(seed));
    }

    private Simulation(SimulationConfig config, Random random) {
        this.config = config;
        this.area = new Area(config.getAreaWidth(), config.getAreaHeight());
        this.random = random;
        for (int i = 0; i < config.getInitialPopulation(); i++) {
            double x = random.nextDouble() * config.getAreaWidth();
            double y = random.nextDouble() * config.getAreaHeight();
            double angle = (random.nextDouble() * 2 - 1) * Math.PI;
            persons.add(createPerson(new Vector2D(x, y), angle));
        }
    }

    /** Jeden krok symulacji (1/25 s). */
    public void step() {
        double dt = 1.0 / config.getStepsPerSecond();
        for (Person p : persons) {
            p.randomizeVelocity(random, config.getMaxSpeed());
            p.move(dt);
        }
        handleBorders();
        tracker.track(persons, config.getInfectionRadius());
        handleInfections();
        for (Person p : persons) {
            p.update();
        }
        spawnPersons();
        stepNumber++;
    }

    /** Osobnik, który wyszedł poza obszar: z prawdopodobieństwem borderExitProb opuszcza go, inaczej zawraca. */
    void handleBorders() {
        List<Person> leaving = new ArrayList<>();
        for (Person p : persons) {
            if (!area.contains(p.getPosition())) {
                if (random.nextDouble() < config.getBorderExitProb()) {
                    leaving.add(p);
                } else {
                    p.turnBack();
                }
            }
        }
        persons.removeAll(leaving);
    }

    /**
     * Zarażenia na podstawie stanów z początku kroku. Gdy para była blisko co najmniej
     * requiredContactSteps kroków, a jedna osoba zaraża i druga jest podatna, losowane jest zarażenie
     * (50% / 100%). Nieudana próba zeruje licznik pary.
     */
    void handleInfections() {
        List<Person> newlyInfected = new ArrayList<>();
        Set<Integer> chosen = new HashSet<>();
        for (int i = 0; i < persons.size(); i++) {
            Person a = persons.get(i);
            for (int j = i + 1; j < persons.size(); j++) {
                Person b = persons.get(j);
                if (tracker.getContactSteps(a, b) < config.getRequiredContactSteps()) continue;
                Person source;
                Person target;
                if (a.getState().isInfectious() && b.getState().isSusceptible()) {
                    source = a;
                    target = b;
                } else if (b.getState().isInfectious() && a.getState().isSusceptible()) {
                    source = b;
                    target = a;
                } else {
                    continue;
                }
                if (chosen.contains(target.getId())) continue;
                if (random.nextDouble() < source.getState().transmissionProbability()) {
                    chosen.add(target.getId());
                    newlyInfected.add(target);
                } else {
                    tracker.reset(a, b);
                }
            }
        }
        for (Person p : newlyInfected) {
            p.setState(StateFactory.randomInfected(random, config));
        }
    }

    /** Z prawdopodobieństwem spawnRate w kroku na losowym punkcie granicy pojawia się nowy osobnik. */
    void spawnPersons() {
        if (random.nextDouble() < config.getSpawnRate()) {
            Vector2D point = area.randomBorderPoint(random);
            persons.add(createPerson(point, inwardAngle(point)));
        }
    }

    /** Tworzy osobnika i losuje jego stan: odporny (immuneRatio), zarażony (entryInfectionProb) lub zdrowy. */
    private Person createPerson(Vector2D position, double angle) {
        double speed = config.getMaxSpeed() * (0.1 + 0.9 * random.nextDouble());
        HealthState state;
        if (random.nextDouble() < config.getImmuneRatio()) {
            state = new ImmuneState();
        } else if (random.nextDouble() < config.getEntryInfectionProb()) {
            state = StateFactory.randomInfected(random, config);
        } else {
            state = new HealthyState();
        }
        return new Person(nextId++, position, Person.velocityOf(angle, speed), state);
    }

    /** Kierunek skierowany do wnętrza obszaru (z najbliższej krawędzi), z losowym odchyleniem do 80 stopni. */
    private double inwardAngle(Vector2D p) {
        double[] c = p.getComponents();
        double w = config.getAreaWidth();
        double h = config.getAreaHeight();
        double dLeft = c[0];
        double dRight = w - c[0];
        double dBottom = c[1];
        double dTop = h - c[1];
        double min = Math.min(Math.min(dLeft, dRight), Math.min(dBottom, dTop));
        double normal;
        if (min == dLeft) normal = 0;
        else if (min == dRight) normal = Math.PI;
        else if (min == dBottom) normal = Math.PI / 2;
        else normal = -Math.PI / 2;
        return normal + (random.nextDouble() * 2 - 1) * Math.toRadians(80);
    }

    public double getTimeSeconds() {
        return stepNumber / (double) config.getStepsPerSecond();
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public Area getArea() {
        return area;
    }

    /** Liczba osobników w danym stanie. */
    public int count(StateType type) {
        int n = 0;
        for (Person p : persons) {
            if (p.getState().getType() == type) n++;
        }
        return n;
    }

    // ---------- Memento (Originator) ----------

    public SimulationMemento createMemento() {
        List<PersonSnapshot> snapshots = new ArrayList<>();
        for (Person p : persons) {
            double[] pos = p.getPosition().getComponents();
            double[] vel = p.getVelocity().getComponents();
            snapshots.add(new PersonSnapshot(p.getId(), pos[0], pos[1], vel[0], vel[1],
                    p.getState().getType(), p.getState().getRemainingSteps()));
        }
        return new SimulationMemento(stepNumber, snapshots, tracker.snapshot(), random, nextId, config);
    }

    public void restore(SimulationMemento memento) {
        this.config = memento.getConfig();
        this.area = new Area(config.getAreaWidth(), config.getAreaHeight());
        this.stepNumber = memento.getStepNumber();
        this.nextId = memento.getNextId();
        this.random = memento.getRandom();
        persons.clear();
        for (PersonSnapshot s : memento.getPersons()) {
            Polar2DAdapter velocity = new Polar2DAdapter(new Vector2D(s.vx(), s.vy()));
            HealthState state = StateFactory.create(s.stateType(), s.remainingSteps());
            persons.add(new Person(s.id(), new Vector2D(s.x(), s.y()), velocity, state));
        }
        tracker.restore(memento.getContacts());
    }
}
