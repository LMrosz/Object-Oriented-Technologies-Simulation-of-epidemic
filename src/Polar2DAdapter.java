public class Polar2DAdapter implements IPolar2D, IVector {
    private Vector2D srcVector;

    public Polar2DAdapter(Vector2D srcVector) {
        this.srcVector = srcVector;
    }

    @Override
    public double abs() {
        return this.srcVector.abs();
    }

    @Override
    public double getAngle() {
        // kąt między osią OX a wektorem
        double[] c = this.srcVector.getComponents();
        return Math.atan2(c[1], c[0]);
    }

    @Override
    public double cdot(IVector param) {
        return this.srcVector.cdot(param);
    }

    @Override
    public double[] getComponents() {
        return this.srcVector.getComponents();
    }
}
