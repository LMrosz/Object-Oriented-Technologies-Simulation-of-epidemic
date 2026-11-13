import java.util.Random;

public class Area {
    private final double width;
    private final double height;

    public Area(double width, double height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Wymiary muszą być dodatnie");
        this.width = width;
        this.height = height;
    }

    public boolean contains(Vector2D vector){
        double[] components = vector.getComponents();
        return components[0] >= 0 && components[0] <= this.width && components[1] >= 0 && components[1] <= this.height;
    }

    public Vector2D randomBorderPoint(Random rand){
        double p = rand.nextDouble() * 2 * (width + height);
        if (p < width)                  return new Vector2D(p, 0);
        if (p < width + height)         return new Vector2D(width, p - width);
        if (p < 2 * width + height)     return new Vector2D(p - width - height, height);
        return new Vector2D(0, p - 2 * width - height);
    }

}

