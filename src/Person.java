import java.util.Random;

/**
 * Osobnik - kontekst we wzorcu Stan. Ruch modelowany wektorami z Lab 2:
 * położenie to Vector2D, prędkość to Polar2DAdapter (kąt + długość),
 * dzięki czemu zmiana kierunku i szybkości jest płynna.
 */
public class Person {
    /** Odchylenie standardowe zmiany kąta w jednym kroku [rad]. */
    private static final double ANGLE_STEP = 0.1;
    /** Odchylenie standardowe zmiany prędkości w jednym kroku, jako ułamek prędkości maksymalnej. */
    private static final double SPEED_STEP_FRACTION = 0.02;
    /** Minimalna prędkość (ułamek maksymalnej) - zapobiega "zgubieniu" kierunku przy prędkości 0. */
    private static final double MIN_SPEED_FRACTION = 0.04;

    private final int id;
    private Vector2D position;
    private Vector2D previousPosition;
    private Polar2DAdapter velocity;
    private HealthState state;

    public Person(int id, Vector2D position, Polar2DAdapter velocity, HealthState state) {
        this.id = id;
        this.position = position;
        this.previousPosition = position;
        this.velocity = velocity;
        this.state = state;
    }

    /** Buduje wektor prędkości z kąta [rad] i szybkości [m/s]. */
    public static Polar2DAdapter velocityOf(double angle, double speed) {
        return new Polar2DAdapter(new Vector2D(speed * Math.cos(angle), speed * Math.sin(angle)));
    }

    /** Krok stanu zdrowotnego (np. odliczanie choroby). */
    public void update() {
        state.update(this);
    }

    /** Przesuwa osobnika o v*dt; poprzednie położenie zapamiętuje na wypadek odbicia od granicy. */
    public void move(double dt) {
        double[] p = position.getComponents();
        double[] v = velocity.getComponents();
        previousPosition = position;
        position = new Vector2D(p[0] + v[0] * dt, p[1] + v[1] * dt);
    }

    /** Losowa, niewielka zmiana kierunku i szybkości; szybkość nigdy nie przekracza maxSpeed. */
    public void randomizeVelocity(Random random, double maxSpeed) {
        double angle = velocity.getAngle() + random.nextGaussian() * ANGLE_STEP;
        double speed = velocity.abs() + random.nextGaussian() * SPEED_STEP_FRACTION * maxSpeed;
        speed = Math.max(MIN_SPEED_FRACTION * maxSpeed, Math.min(maxSpeed, speed));
        velocity = velocityOf(angle, speed);
    }

    /** Zawraca osobnika: kierunek o 180 stopni i powrót na położenie sprzed ruchu (do wnętrza obszaru). */
    public void turnBack() {
        velocity = velocityOf(velocity.getAngle() + Math.PI, velocity.abs());
        position = previousPosition;
    }

    public double distanceTo(Person other) {
        double[] a = position.getComponents();
        double[] b = other.position.getComponents();
        double dx = a[0] - b[0];
        double dy = a[1] - b[1];
        return Math.sqrt(dx * dx + dy * dy); // odległość euklidesowa: sqrt(dx^2 + dy^2)
    }

    public int getId() {
        return id;
    }

    public Vector2D getPosition() {
        return position;
    }

    public Polar2DAdapter getVelocity() {
        return velocity;
    }

    public HealthState getState() {
        return state;
    }

    public void setState(HealthState state) {
        this.state = state;
    }
}
