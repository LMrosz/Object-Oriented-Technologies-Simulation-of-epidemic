public class Vector2D implements IVector {
    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double[] getComponents() {
        return new double[]{this.x, this.y};
    }

    @Override
    public double abs() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @Override
    public double cdot(IVector param) {
        double[] other = param.getComponents();

        return this.x * other[0] + this.y * other[1];
    }
}
