import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Rysuje obszar symulacji i osobników; kolor oznacza stan zdrowotny. */
public class SimulationPanel extends JPanel {
    private static final int MARGIN = 16;

    private Simulation simulation;

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.WHITE);
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        repaint();
    }

    /** Kolor osobnika zależny od stanu: odporny - niebieski, zdrowy - zielony, bez objawów - pomarańczowy, z objawami - czerwony. */
    static Color colorOf(HealthState state) {
        switch (state.getType()) {
            case IMMUNE:
                return new Color(41, 98, 255);
            case HEALTHY:
                return new Color(46, 160, 67);
            case INFECTED_ASYMPTOMATIC:
                return new Color(255, 152, 0);
            default:
                return new Color(214, 40, 40);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SimulationConfig cfg = simulation.getConfig();
        double scale = Math.min((getWidth() - 2.0 * MARGIN) / cfg.getAreaWidth(),
                                (getHeight() - 2.0 * MARGIN) / cfg.getAreaHeight());
        int ox = MARGIN;
        int oy = MARGIN;
        int w = (int) Math.round(cfg.getAreaWidth() * scale);
        int h = (int) Math.round(cfg.getAreaHeight() * scale);

        g2.setColor(new Color(245, 247, 250));
        g2.fillRect(ox, oy, w, h);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(ox, oy, w, h);

        double radiusPx = Math.max(3.0, 0.5 * scale); // osobnik ma ok. 1 m średnicy
        for (Person p : simulation.getPersons()) {
            double[] c = p.getPosition().getComponents();
            double px = ox + c[0] * scale;
            double py = oy + h - c[1] * scale; // oś Y skierowana w górę
            if (p.getState().isInfectious()) {
                // przerywany okrąg o promieniu zakażenia (2 m)
                double r = cfg.getInfectionRadius() * scale;
                g2.setColor(new Color(214, 40, 40, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval((int) Math.round(px - r), (int) Math.round(py - r), (int) Math.round(2 * r), (int) Math.round(2 * r));
            }
            g2.setColor(colorOf(p.getState()));
            g2.fillOval((int) Math.round(px - radiusPx), (int) Math.round(py - radiusPx),
                        (int) Math.round(2 * radiusPx), (int) Math.round(2 * radiusPx));
        }
        drawLegend(g2, ox + 8, oy + 8);
        g2.dispose();
    }

    private void drawLegend(Graphics2D g2, int x, int y) {
        String[] labels = {"zdrowy", "zarażony bez objawów", "zarażony z objawami", "odporny"};
        HealthState[] states = {new HealthyState(), new AsymptomaticState(1), new SymptomaticState(1), new ImmuneState()};
        FontMetrics fm = g2.getFontMetrics();
        int rowH = fm.getHeight() + 2;
        int boxW = 0;
        for (String s : labels) boxW = Math.max(boxW, fm.stringWidth(s));
        g2.setColor(new Color(255, 255, 255, 215));
        g2.fillRoundRect(x - 4, y - 4, boxW + 32, rowH * labels.length + 6, 8, 8);
        for (int i = 0; i < labels.length; i++) {
            int yy = y + i * rowH;
            g2.setColor(colorOf(states[i]));
            g2.fillOval(x, yy + 2, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString(labels[i], x + 16, yy + fm.getAscent());
        }
    }
}
